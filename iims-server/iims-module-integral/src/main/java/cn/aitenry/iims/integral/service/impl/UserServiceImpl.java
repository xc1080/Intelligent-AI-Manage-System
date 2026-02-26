package cn.aitenry.iims.integral.service.impl;

import cn.aitenry.iims.common.model.entity.integral.User;
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
import cn.aitenry.iims.common.model.dto.BaseUserInfoQueryDTO;
import cn.aitenry.iims.common.model.entity.base.BaseUserInfo;
import cn.aitenry.iims.common.model.entity.integral.Organization;
import cn.aitenry.iims.common.model.vo.BaseUserInfoVO;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.common.service.FileStorageService;
import cn.aitenry.iims.common.service.MinioService;
import cn.aitenry.iims.common.utils.RSAUtil;
import cn.aitenry.iims.common.utils.RegexUtil;
import cn.aitenry.iims.integral.mapper.UserMapper;
import cn.aitenry.iims.integral.model.dto.user.UserDTO;
import cn.aitenry.iims.integral.model.dto.user.UserLoginDTO;
import cn.aitenry.iims.integral.model.dto.user.UserPageQueryDTO;
import cn.aitenry.iims.integral.model.dto.user.UserPasswordDTO;
import cn.aitenry.iims.integral.model.entity.PublicKey;
import cn.aitenry.iims.integral.model.vo.user.UserMenuVO;
import cn.aitenry.iims.integral.model.vo.user.UserVO;
import cn.aitenry.iims.integral.service.UserService;
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
public class UserServiceImpl implements UserService {


    private final UserMapper userMapper;

    private final MinioService minioService;

    private final FileStorageService storageService;

    private final OrganizationService organizationService;

    private final StringRedisTemplate redisTemplate;

    private volatile String currentPublicKey;

    private volatile String currentPrivateKey;

    public UserServiceImpl(
            UserMapper userMapper, MinioService minioService,
            FileStorageService storageService, OrganizationService organizationService, StringRedisTemplate redisTemplate) {
        this.userMapper = userMapper;
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
     * 用户注册
     *
     * @param userDTO UserDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UserDTO userDTO) {
        //创建一个用户实体类
        User user = new User();
        //属性拷贝
        BeanUtils.copyProperties(userDTO, user);
        //设置密码，默认密码
        user.setPassword(DigestUtils.md5DigestAsHex(
                PasswordConstant.resetPassword(user.getIdNumber()).getBytes()
        ));
        if (userMapper.insert(user) > 0) {
            storageService.updateFileStatus(user.getAvatar(), FileStatusEnum.USED);
        }
    }

    /**
     * 用户登录
     *
     * @param userLoginDto UserLoginDto
     * @return User
     */
    @Override
    public User login(UserLoginDTO userLoginDto) {
        String email = userLoginDto.getEmail();
        String username = userLoginDto.getUsername();
        String password = userLoginDto.getPassword();
        String phone = userLoginDto.getPhone();
        String idNumber = userLoginDto.getIdNumber();

        if (password == null) throw new PasswordErrorException(MessageConstant.PASSWORD_IS_NOT_NULL);

        String redisKey = RSAUtil.generateRedisRSAKey(userLoginDto.getUuid());
        String base64PrivateKey = redisTemplate.opsForValue().get(redisKey);

        try {
            password = RSAUtil.decrypt(password, base64PrivateKey);
        } catch (Exception e) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        } finally {
            redisTemplate.delete(redisKey);
        }

        //1、根据用户名或者邮箱或者手机号或者身份证号码查询数据库中的数据
        User user = null;
        if ((StringUtils.isNotEmpty(email) && RegexUtil.isMail(email)) || StringUtils.isNotEmpty(username) ||
                (StringUtils.isNotEmpty(phone) && RegexUtil.isCellPhoneNo(phone)) ||
                (StringUtils.isNotEmpty(idNumber) && RegexUtil.isCheckIdNumber(idNumber))
        ) {
            user = userMapper.getByUserNameOrEmail(userLoginDto);
        }

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getIsDisable()) {
            //账户被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        if (user.getIsDeleted()) {
            //账号已被移除
            throw new AccountLockedException(MessageConstant.ACCOUNT_REMOVE);
        }

        user.setImageUrl(minioService.generateShortLink(user.getAvatar()));

        //返回实体对象
        return user;
    }

    /**
     * 用户分页查询
     *
     * @param userPageQueryDTO UserPageQueryDTO
     * @return PageResult
     */
    @Override
    public PageResult pageQuery(UserPageQueryDTO userPageQueryDTO) {
        int page = userPageQueryDTO.getPage();
        int pageSize = userPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        Page<UserVO> admins = userMapper.pageQuery(userPageQueryDTO);
        long total = admins.getTotal();
        List<UserVO> records = admins.getResult();

        for (UserVO admin : records) {
            List<String> list = JSONObject.parseArray(
                    JSON.parseArray(admin.getRole()).toJSONString(), String.class
            );
            List<String> roleNameByIds = userMapper.getRoleNameByIds(list);
            admin.setImageUrl(minioService.generateShortLink(admin.getAvatar()));
            admin.setRole(JSONArray.toJSONString(list));
            admin.setRoles(roleNameByIds);
        }

        return new PageResult(total, records);
    }

    /**
     * 启用禁用用户账号
     *
     * @param isDisable 禁用状态
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(Boolean isDisable, Long id) {
        User user = new User();
        user.setId(id);
        user.setIsDisable(isDisable);
        userMapper.update(user);
    }

    /**
     * 编辑用户信息
     *
     * @param userDto UserDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserDTO userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        Long oldFileId = userMapper.getAvatarById(user.getId());
        Long newFileId = user.getAvatar();
        if (userMapper.update(user) > 0) {
            storageService.changeFile(oldFileId, 0, newFileId, 1);
        }

    }

    /**
     * 根据id查询用户信息
     *
     * @param id 用户ID
     * @return User
     */
    @Override
    public User getById(Long id) {
        return userMapper.selectObjectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        User user = userMapper.selectObjectById(id);
        if (user == null) throw new NotFoundException(MessageConstant.DATA_NOT_FOUND);
        User userDto = User.builder().id(id).isDeleted(true).build();
        if (userMapper.update(userDto) > 0) {
            storageService.updateFileStatus(user.getAvatar(), FileStatusEnum.DELETE);
        }
    }

    /**
     * 修改密码
     *
     * @param userPasswordDto UserPasswordDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UserPasswordDTO userPasswordDto) {
        //根据前端传过来的id查询用户信息
        User user = userMapper.selectObjectById(userPasswordDto.getId());
        //查询当前用户的id
        long id = StpUtil.getLoginIdAsLong();
        //处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        //用户名不存在
        if (user == null)
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        //账号被锁定
        if (user.getIsDisable())
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        //判断当前登录的用户只修改自己的密码，不允许修改别人密码
        if (!user.getId().equals(id))
            throw new AccountNotFoundException(MessageConstant.NOT_CURRENT_USER);

        String oldPassword = DigestUtils.md5DigestAsHex(userPasswordDto.getOldPassword().getBytes());
        String newPassword = DigestUtils.md5DigestAsHex(userPasswordDto.getNewPassword().getBytes());
        //判断前端传过来的旧密码和数据库密码是否一致
        if (!user.getPassword().equals(oldPassword))
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        //判断前端传过来的新密码和数据密码是否一致（原理上新密码和旧密码不能一样）
        if (user.getPassword().equals(newPassword))
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_SAME_NEW_PASSWORD);
        user.setPassword(newPassword);
        userMapper.update(user);
    }

    /**
     * 导出用户数据
     *
     * @param response HttpServletResponse
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        List<User> list = userMapper.list();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("template/UserExcel.xlsx");

        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            assert inputStream != null;
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            XSSFRow row;
            for (int i = 0; i < list.size(); i++) {
                User user = list.get(i);
                //准备明细数据
                row = sheet.getRow(2 + i);
                row.getCell(0).setCellValue(user.getId());
                row.getCell(1).setCellValue(user.getName());
                row.getCell(2).setCellValue(user.getUsername());
                row.getCell(3).setCellValue(user.getIdNumber());
                row.getCell(4).setCellValue(user.getEmail());
                row.getCell(5).setCellValue(user.getPhone());
                row.getCell(6).setCellValue(user.getAvatar());
                row.getCell(7).setCellValue(user.getSex());
                row.getCell(8).setCellValue(user.getRole());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                LocalDateTime createTime = user.getCreateTime();
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
    public List<BaseUserInfo> baseInfoQuery(BaseUserInfoQueryDTO baseInfoQueryDto) {
        return userMapper.baseInfoQuery(baseInfoQueryDto.getName());
    }

    @Override
    public BaseUserInfoVO getUserBaseInfoById(Long id) {
        User user = userMapper.selectObjectById(id);
        Long organization = user.getOrganization();
        Organization department = organizationService.getDepartmentByJobId(organization);
        return BaseUserInfoVO.builder().id(user.getId()).username(user.getUsername())
                .email(user.getEmail()).phone(user.getPhone())
                .imageUrl(minioService.generateShortLink(user.getAvatar()))
                .department(department.getName()).introduction(user.getIntroduction()).build();
    }

    @Override
    public List<BaseUserInfoVO> getUserBaseInfoByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询用户信息
        List<User> users = userMapper.selectObjectByIds(ids);

        // 提取所有组织ID
        Set<Long> organizationIds = users.stream()
                .map(User::getOrganization)
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
        return users.stream()
                .map(admin -> {
                    Organization department = organizationMap.get(admin.getOrganization());
                    return BaseUserInfoVO.builder()
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
    public HashMap<Long, BaseUserInfoVO> getUserBaseHashInfoByIds(List<Long> ids) {
        List<BaseUserInfoVO> userBaseInfoByIds = this.getUserBaseInfoByIds(ids);
        HashMap<Long, BaseUserInfoVO> hash = new HashMap<>();
        userBaseInfoByIds.forEach(baseUserInfoVO -> hash.put(baseUserInfoVO.getId(), baseUserInfoVO));
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
     * @param id 用户ID
     * @return List<UserMenuVO>
     */
    @Override
    public List<UserMenuVO> getMenusByUserId(Long id) {
        //拿到菜单的所有数据
        List<UserMenuVO> list = userMapper.getMenusByUserId(id);
        //存储根节点的菜单，即一级菜单
        List<UserMenuVO> rootList = new ArrayList<>();
        //遍历所有数据，找到根节点菜单
        for (UserMenuVO userMenuVo : list) {
            if (userMenuVo.getParentId() == 0) {
                //找到根节点菜单的时候，寻找这个根节点菜单下的子节点菜单。
                findChild(userMenuVo, list);
                //添加到根节点的列表中
                rootList.add(userMenuVo);
            }
        }
        return rootList;
    }

    /**
     * 根据根节点菜单寻找子节点菜单
     *
     * @param root UserMenuVO
     * @param list List<UserMenuVO>
     */
    private void findChild(UserMenuVO root, List<UserMenuVO> list) {
        List<UserMenuVO> childList = new ArrayList<>();
        //遍历所有数据，找到是入参父节点的子节点的数据，然后加到childList集合中。
        for (UserMenuVO menu : list) {
            if (root.getId().equals(menu.getParentId()))
                childList.add(menu);
        }
        //若子节点不存在，那么就不必再遍历子节点中的子节点了 直接返回。
        if (childList.isEmpty())
            return;
        //设置父节点的子节点列表
        root.setChildren(childList);
        //若子节点存在，接着递归调用该方法，寻找子节点的子节点。
        for (UserMenuVO child : childList) {
            findChild(child, list);
        }
    }
}