package cn.aitenry.iims.integral.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONArray;
import cn.aitenry.iims.common.model.dto.BaseUserInfoQueryDTO;
import cn.aitenry.iims.common.model.entity.base.BaseUserInfo;
import cn.aitenry.iims.common.model.entity.integral.User;
import cn.aitenry.iims.common.model.vo.BaseUserInfoVO;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.common.result.Result;
import cn.aitenry.iims.integral.model.dto.user.UserDTO;
import cn.aitenry.iims.integral.model.dto.user.UserLoginDTO;
import cn.aitenry.iims.integral.model.dto.user.UserPageQueryDTO;
import cn.aitenry.iims.integral.model.dto.user.UserPasswordDTO;
import cn.aitenry.iims.integral.model.entity.PublicKey;
import cn.aitenry.iims.integral.model.vo.user.UserVO;
import cn.aitenry.iims.integral.model.vo.role.RoleVO;
import cn.aitenry.iims.integral.service.UserService;
import cn.aitenry.iims.integral.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@RestController
@RequestMapping("/iims/user")
@Slf4j
@Api(tags = "用户")
public class UserController implements Serializable {


    private final UserService userService;

    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * 用户注册
     *
     * @param userDTO UserDTO
     * @return Result<String>
     */
    @PostMapping()
    @ApiOperation("用户注册")
    public Result<String> save(@RequestBody UserDTO userDTO) {
        userService.save(userDTO);
        return Result.success("成功新增用户");
    }

    @GetMapping("/login/key")
    public Result<PublicKey> getPublicKey() {
        return Result.success(userService.getPublicKey());
    }

    /**
     * 用户登录
     *
     * @param userLoginDto UserLoginDTO
     * @return Result<UserVO>
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@RequestBody @Valid UserLoginDTO userLoginDto) {
        User user = userService.login(userLoginDto);
        //登录成功后，生成jwt令牌
        String uid = String.valueOf(user.getId());
        String token = null;
        List<String> permissions = null;
        if (uid != null) {
            StpUtil.logout(uid, "Web");
            StpUtil.login(uid, "Web");
            token = StpUtil.getTokenValueByLoginId(uid, "Web");
            permissions = StpUtil.getPermissionList(uid);
        }

        assert uid != null;
        List<Long> roleIds = JSONArray.parseArray(user.getRole(), Long.class);
        String roleName = null;
        if (Objects.nonNull(roleIds) && !roleIds.isEmpty()) {
            RoleVO roleVo = roleService.getById(roleIds.get(0));
            roleName = roleVo.getRoleName();
        }
        UserVO userVo = UserVO.builder()
                .id(Long.valueOf(uid))
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(roleName)
                .imageUrl(user.getImageUrl())
                .token(token)
                .permissions(permissions)
                .build();

        return Result.successWithNonNull(userVo);
    }

    @GetMapping("/logout")
    @ApiOperation("用户退出")
    public Result<String> logout() {
        //从令牌中解密出来userId
        long id = StpUtil.getLoginIdAsLong();
        //销毁令牌
        StpUtil.logout(id, "Web");
        return Result.success();
    }

    /**
     * 用户分页查询
     *
     * @param userPageQueryDTO 查询用户分类数据
     * @return Result<PageResult>
     */
    @PostMapping("/page")
    @ApiOperation("用户分页查询")
    @SaCheckPermission("permission:admin:query")
    public Result<PageResult> page(@RequestBody UserPageQueryDTO userPageQueryDTO) {
        PageResult pageResult = userService.pageQuery(userPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/base/page")
    @ApiOperation("用户分页查询")
    @SaCheckPermission("permission:admin:query")
    public Result<List<BaseUserInfo>> info(@RequestBody BaseUserInfoQueryDTO baseInfoQueryDto) {
        List<BaseUserInfo> result = userService.baseInfoQuery(baseInfoQueryDto);
        return Result.success(result);
    }

    /**
     * 启用禁用用户账号
     *
     * @param isDisable 禁用状态
     * @param id 用户id
     * @return Result<String>
     */
    @PostMapping("/status/{isDisable}")
    @ApiOperation("启用禁用用户账号")
    public Result<String> startOrStop(@PathVariable Boolean isDisable, Long id) {
        userService.startOrStop(isDisable, id);
        return Result.success();
    }

    /**
     * 编辑用户信息
     *
     * @param userDto UserDTO
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("编辑用户信息")
    public Result<String> update(@RequestBody UserDTO userDto) {
        userService.update(userDto);
        return Result.success();
    }

    /**
     * 根据id查询用户信息
     *
     * @param id 用户ID
     * @return Result<User>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户信息")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @GetMapping("/base/info/{id}")
    @ApiOperation("根据id查询基础用户信息")
    public Result<BaseUserInfoVO> getUserBaseInfoById(@PathVariable Long id) {
        BaseUserInfoVO admin = userService.getUserBaseInfoById(id);
        return Result.success(admin);
    }

    /**
     * 根据id删除用户
     *
     * @param id ID
     * @return Result<String>
     */
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除用户")
    @SaCheckPermission("permission:admin:delete")
    public Result<String> deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param userPasswordDto UserPasswordDTO
     * @return Result<String>
     */
    @PutMapping("/password")
    @ApiOperation("修改密码")
    @CacheEvict(value = "adminCache", allEntries = true)
    public Result<String> updatePassword(@RequestBody UserPasswordDTO userPasswordDto) {
        userService.updatePassword(userPasswordDto);
        return Result.success();
    }

    /**
     * 导出模板
     *
     * @param response HttpServletResponse
     */
    @GetMapping("/export")
    @ApiOperation("导出模板")
    public Result<String> export(HttpServletResponse response) {
        userService.exportBusinessData(response);
        return Result.success();
    }

    /**
     * 导入用户数据报表
     *
     * @param file MultipartFile
     * @return Result<List<UserVO>>
     */
    @PostMapping("/import")
    @ApiOperation("导入用户数据报表")
    public Result<List<UserVO>> importUser(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        //通过输入流读取指定的Excel文件
        XSSFWorkbook excel = new XSSFWorkbook(in);
        //获取Excel文件的第1个Sheet页
        XSSFSheet sheet = excel.getSheet("Sheet1");

        //获取Sheet页中的最后一行的行号
        int lastRowNum = sheet.getLastRowNum();

        ArrayList<UserVO> list = new ArrayList<>();

        for (int i = 2; i <= lastRowNum; i++) {
            UserVO admin = new UserVO();

            //获取Sheet页中的行
            XSSFRow titleRow = sheet.getRow(i);

            //真实姓名
            XSSFCell cell2 = titleRow.getCell(0);
            String name = cell2.getStringCellValue();
            admin.setName(name);

            //用户名
            XSSFCell cell3 = titleRow.getCell(1);
            String username = cell3.getStringCellValue();
            admin.setUsername(username);

            //邮箱
            XSSFCell cell5 = titleRow.getCell(3);
            String email = cell5.getStringCellValue();
            admin.setEmail(email);

            //手机号
            XSSFCell cell6 = titleRow.getCell(4);
            String phone = cell6.getStringCellValue();
            admin.setPhone(phone);

            //头像
            XSSFCell cell7 = titleRow.getCell(5);
            String avatar = cell7.getStringCellValue();
            admin.setAvatar(Long.valueOf(avatar));

            //性别
            XSSFCell cell8 = titleRow.getCell(6);
            String sex = cell8.getStringCellValue();
            if (sex.equals("男")) {
                sex = "1";
            } else {
                sex = "2";
            }
            admin.setSex(sex);

            //角色
            admin.setRole("[11]");

            list.add(admin);
        }

        //关闭资源
        in.close();
        excel.close();

        return Result.success(list);
    }
}
