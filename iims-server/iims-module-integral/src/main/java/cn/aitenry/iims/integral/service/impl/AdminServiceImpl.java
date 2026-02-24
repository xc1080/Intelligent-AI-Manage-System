package cn.aitenry.iims.integral.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.aitenry.iims.common.constant.MessageConstant;
import cn.aitenry.iims.common.constant.PasswordConstant;
import cn.aitenry.iims.common.enums.FileStatusEnum;
import cn.aitenry.iims.common.exception.AccountLockedException;
import cn.aitenry.iims.common.exception.AccountNotFoundException;
import cn.aitenry.iims.common.exception.NotFoundException;
import cn.aitenry.iims.common.exception.PasswordErrorException;
import cn.aitenry.iims.common.model.dto.BaseAdminInfoQueryDTO;
import cn.aitenry.iims.common.model.entity.base.BaseAdminInfo;
import cn.aitenry.iims.common.model.entity.integral.Admin;
import cn.aitenry.iims.common.model.entity.integral.Organization;
import cn.aitenry.iims.common.model.vo.BaseAdminInfoVO;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.common.service.FileStorageService;
import cn.aitenry.iims.common.service.MinioService;
import cn.aitenry.iims.common.utils.RSAUtil;
import cn.aitenry.iims.common.utils.RegexUtil;
import cn.aitenry.iims.integral.mapper.AdminMapper;
import cn.aitenry.iims.integral.model.dto.admin.AdminDTO;
import cn.aitenry.iims.integral.model.dto.admin.AdminLoginDTO;
import cn.aitenry.iims.integral.model.dto.admin.AdminPageQueryDTO;
import cn.aitenry.iims.integral.model.dto.admin.AdminPasswordDTO;
import cn.aitenry.iims.integral.model.entity.PublicKey;
import cn.aitenry.iims.integral.model.vo.admin.AdminMenuVO;
import cn.aitenry.iims.integral.model.vo.admin.AdminVO;
import cn.aitenry.iims.integral.service.AdminService;
import cn.aitenry.iims.integral.service.OrganizationService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {


    private final AdminMapper adminMapper;

    private final MinioService minioService;

    private final FileStorageService storageService;

    private final OrganizationService organizationService;

    private final StringRedisTemplate redisTemplate;

    private volatile String currentPublicKey;

    private volatile String currentPrivateKey;

    public AdminServiceImpl(
            AdminMapper adminMapper, MinioService minioService,
            FileStorageService storageService, OrganizationService organizationService, StringRedisTemplate redisTemplate) {
        this.adminMapper = adminMapper;
        this.minioService = minioService;
        this.storageService = storageService;
        this.organizationService = organizationService;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initializeKeyPair() {
        refreshKeyPair();
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // 每5分钟刷新一次
    public void scheduledRefreshKeyPair() {
        refreshKeyPair();
    }

    private void refreshKeyPair() {
        try {
            KeyPair newKeyPair = RSAUtil.generateRSAKeyPair();
            String publicKey = RSAUtil.encodePublicKey(newKeyPair);
            String privateKey = RSAUtil.encodePrivateKey(newKeyPair);

            this.currentPublicKey = publicKey;
            this.currentPrivateKey = privateKey;
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to refresh RSA key pair", e);
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }

    /**
     * 管理员注册
     *
     * @param adminRegisterDto AdminDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(AdminDTO adminRegisterDto) {
        //创建一个管理员实体类
        Admin admin = new Admin();
        //属性拷贝
        BeanUtils.copyProperties(adminRegisterDto, admin);
        //设置密码，默认密码
        admin.setPassword(DigestUtils.md5DigestAsHex(
                PasswordConstant.resetPassword(admin.getIdNumber()).getBytes()
        ));
        if (adminMapper.insert(admin) > 0) {
            storageService.updateFileStatus(admin.getAvatar(), FileStatusEnum.USED);
        }
    }

    /**
     * 管理员登录
     *
     * @param adminLoginDto AdminLoginDto
     * @return Admin
     */
    @Override
    public Admin login(AdminLoginDTO adminLoginDto) {
        String email = adminLoginDto.getEmail();
        String username = adminLoginDto.getUsername();
        String password = adminLoginDto.getPassword();
        String phone = adminLoginDto.getPhone();
        String idNumber = adminLoginDto.getIdNumber();

        if (password == null) throw new PasswordErrorException(MessageConstant.PASSWORD_IS_NOT_NULL);

        String redisKey = RSAUtil.generateRedisRSAKey(adminLoginDto.getUuid());
        String base64PrivateKey = redisTemplate.opsForValue().get(redisKey);

        try {
            password = RSAUtil.decrypt(password, base64PrivateKey);
        } catch (Exception e) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        } finally {
            redisTemplate.delete(redisKey);
        }

        //1、根据用户名或者邮箱或者手机号或者身份证号码查询数据库中的数据
        Admin admin = null;
        if ((StringUtils.isNotEmpty(email) && RegexUtil.isMail(email)) || StringUtils.isNotEmpty(username) ||
                (StringUtils.isNotEmpty(phone) && RegexUtil.isCellPhoneNo(phone)) ||
                (StringUtils.isNotEmpty(idNumber) && RegexUtil.isCheckIdNumber(idNumber))
        ) {
            admin = adminMapper.getByUserNameOrEmail(adminLoginDto);
        }

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (admin == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(admin.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (admin.getIsDisable()) {
            //账户被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        if (admin.getIsDeleted()) {
            //账号已被移除
            throw new AccountLockedException(MessageConstant.ACCOUNT_REMOVE);
        }

        admin.setImageUrl(minioService.generateShortLink(admin.getAvatar()));

        //返回实体对象
        return admin;
    }

    /**
     * 管理员分页查询
     *
     * @param adminPageQueryDTO AdminPageQueryDTO
     * @return PageResult
     */
    @Override
    public PageResult pageQuery(AdminPageQueryDTO adminPageQueryDTO) {
        int page = adminPageQueryDTO.getPage();
        int pageSize = adminPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        Page<AdminVO> admins = adminMapper.pageQuery(adminPageQueryDTO);
        long total = admins.getTotal();
        List<AdminVO> records = admins.getResult();

        for (AdminVO admin : records) {
            List<String> list = JSONObject.parseArray(
                    JSON.parseArray(admin.getRole()).toJSONString(), String.class
            );
            List<String> roleNameByIds = adminMapper.getRoleNameByIds(list);
            admin.setImageUrl(minioService.generateShortLink(admin.getAvatar()));
            admin.setRole(JSONArray.toJSONString(list));
            admin.setRoles(roleNameByIds);
        }

        return new PageResult(total, records);
    }

    /**
     * 启用禁用管理员账号
     *
     * @param isDisable 禁用状态
     * @param id 管理员ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(Boolean isDisable, Long id) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setIsDisable(isDisable);
        adminMapper.update(admin);
    }

    /**
     * 编辑管理员信息
     *
     * @param adminDto AdminDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AdminDTO adminDto) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDto, admin);
        Long oldFileId = adminMapper.getAvatarById(admin.getId());
        Long newFileId = admin.getAvatar();
        if (adminMapper.update(admin) > 0) {
            storageService.changeFile(oldFileId, 0, newFileId, 1);
        }

    }

    /**
     * 根据id查询管理员信息
     *
     * @param id 管理员ID
     * @return Admin
     */
    @Override
    public Admin getById(Long id) {
        return adminMapper.selectObjectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        Admin admin = adminMapper.selectObjectById(id);
        if (admin == null) throw new NotFoundException(MessageConstant.DATA_NOT_FOUND);
        Admin adminDto = Admin.builder().id(id).isDeleted(true).build();
        if (adminMapper.update(adminDto) > 0) {
            storageService.updateFileStatus(admin.getAvatar(), FileStatusEnum.DELETE);
        }
    }

    /**
     * 修改密码
     *
     * @param adminPasswordDto AdminPasswordDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(AdminPasswordDTO adminPasswordDto) {
        //根据前端传过来的id查询用户信息
        Admin admin = adminMapper.selectObjectById(adminPasswordDto.getId());
        //查询当前用户的id
        long id = StpUtil.getLoginIdAsLong();
        //处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        //用户名不存在
        if (admin == null)
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        //账号被锁定
        if (admin.getIsDisable())
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        //判断当前登录的用户只修改自己的密码，不允许修改别人密码
        if (!admin.getId().equals(id))
            throw new AccountNotFoundException(MessageConstant.NOT_CURRENT_USER);

        String oldPassword = DigestUtils.md5DigestAsHex(adminPasswordDto.getOldPassword().getBytes());
        String newPassword = DigestUtils.md5DigestAsHex(adminPasswordDto.getNewPassword().getBytes());
        //判断前端传过来的旧密码和数据库密码是否一致
        if (!admin.getPassword().equals(oldPassword))
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        //判断前端传过来的新密码和数据密码是否一致（原理上新密码和旧密码不能一样）
        if (admin.getPassword().equals(newPassword))
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_SAME_NEW_PASSWORD);
        admin.setPassword(newPassword);
        adminMapper.update(admin);
    }

    /**
     * 导出用户数据
     *
     * @param response HttpServletResponse
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        List<Admin> list = adminMapper.list();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("template/AdminExcel.xlsx");

        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            assert inputStream != null;
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            XSSFRow row;
            for (int i = 0; i < list.size(); i++) {
                Admin admin = list.get(i);
                //准备明细数据
                row = sheet.getRow(2 + i);
                row.getCell(0).setCellValue(admin.getId());
                row.getCell(1).setCellValue(admin.getName());
                row.getCell(2).setCellValue(admin.getUsername());
                row.getCell(3).setCellValue(admin.getIdNumber());
                row.getCell(4).setCellValue(admin.getEmail());
                row.getCell(5).setCellValue(admin.getPhone());
                row.getCell(6).setCellValue(admin.getAvatar());
                row.getCell(7).setCellValue(admin.getSex());
                row.getCell(8).setCellValue(admin.getRole());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                LocalDateTime createTime = admin.getCreateTime();
                Date date = Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant());
                String format = sdf.format(date);
                row.getCell(9).setCellValue(format);
            }

            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<BaseAdminInfo> baseInfoQuery(BaseAdminInfoQueryDTO baseInfoQueryDto) {
        return adminMapper.baseInfoQuery(baseInfoQueryDto.getUsername());
    }

    @Override
    public BaseAdminInfoVO getAdminBaseInfoById(Long id) {
        Admin admin = adminMapper.selectObjectById(id);
        Long organization = admin.getOrganization();
        Organization department = organizationService.getDepartmentByJobId(organization);
        return BaseAdminInfoVO.builder().id(admin.getId()).username(admin.getUsername())
                .email(admin.getEmail()).phone(admin.getPhone())
                .imageUrl(minioService.generateShortLink(admin.getAvatar()))
                .department(department.getName()).introduction(admin.getIntroduction()).build();
    }

    @Override
    public List<BaseAdminInfoVO> getAdminBaseInfoByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询管理员信息
        List<Admin> admins = adminMapper.selectObjectByIds(ids);

        // 提取所有组织ID
        Set<Long> organizationIds = admins.stream()
                .map(Admin::getOrganization)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 批量查询组织信息
        Map<Long, Organization> organizationMap;
        if (!organizationIds.isEmpty()) {
            List<Organization> departments = organizationService.getDepartmentsByJobIds(new ArrayList<>(organizationIds));
            organizationMap = departments.stream()
                    .collect(Collectors.toMap(Organization::getJobId, department -> department));
        } else {
            organizationMap = new HashMap<>();
        }

        // 构建结果
        return admins.stream()
                .map(admin -> {
                    Organization department = organizationMap.get(admin.getOrganization());
                    return BaseAdminInfoVO.builder()
                            .id(admin.getId())
                            .username(admin.getUsername())
                            .email(admin.getEmail())
                            .phone(admin.getPhone())
                            .imageUrl(minioService.generateShortLink(admin.getAvatar()))
                            .department(department != null ? department.getName() : null)
                            .introduction(admin.getIntroduction())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public HashMap<Long, BaseAdminInfoVO> getAdminBaseHashInfoByIds(List<Long> ids) {
        List<BaseAdminInfoVO> adminBaseInfoByIds = this.getAdminBaseInfoByIds(ids);
        HashMap<Long, BaseAdminInfoVO> hash = new HashMap<>();
        adminBaseInfoByIds.forEach(baseAdminInfoVO -> hash.put(baseAdminInfoVO.getId(), baseAdminInfoVO));
        return hash;
    }

    @Override
    public PublicKey getPublicKey() {
        String uuid = UUID.randomUUID().toString();

        // 使用当前的私钥存入 Redis
        redisTemplate.opsForValue().set(
                RSAUtil.generateRedisRSAKey(uuid),
                currentPrivateKey,
                5, TimeUnit.MINUTES
        );

        return PublicKey.builder()
                .publicKey(currentPublicKey)
                .uuid(uuid)
                .build();
    }

    /**
     * 采用递归方法，遍历成树级结构菜单
     * 根据用户id查询菜单集合【获得树级结构菜单】
     *
     * @param id 管理员ID
     * @return List<AdminMenuVo>
     */
    @Override
    public List<AdminMenuVO> getMenusByUserId(Long id) {
        //拿到菜单的所有数据
        List<AdminMenuVO> list = adminMapper.getMenusByUserId(id);
        //存储根节点的菜单，即一级菜单
        List<AdminMenuVO> rootList = new ArrayList<>();
        //遍历所有数据，找到根节点菜单
        for (AdminMenuVO adminMenuVo : list) {
            if (adminMenuVo.getParentId() == 0) {
                //找到根节点菜单的时候，寻找这个根节点菜单下的子节点菜单。
                findChild(adminMenuVo, list);
                //添加到根节点的列表中
                rootList.add(adminMenuVo);
            }
        }
        return rootList;
    }

    /**
     * 根据根节点菜单寻找子节点菜单
     *
     * @param root AdminMenuVo
     * @param list List<AdminMenuVo>
     */
    private void findChild(AdminMenuVO root, List<AdminMenuVO> list) {
        List<AdminMenuVO> childList = new ArrayList<>();
        //遍历所有数据，找到是入参父节点的子节点的数据，然后加到childList集合中。
        for (AdminMenuVO menu : list) {
            if (root.getId().equals(menu.getParentId()))
                childList.add(menu);
        }
        //若子节点不存在，那么就不必再遍历子节点中的子节点了 直接返回。
        if (childList.isEmpty())
            return;
        //设置父节点的子节点列表
        root.setChildren(childList);
        //若子节点存在，接着递归调用该方法，寻找子节点的子节点。
        for (AdminMenuVO child : childList) {
            findChild(child, list);
        }
    }
}