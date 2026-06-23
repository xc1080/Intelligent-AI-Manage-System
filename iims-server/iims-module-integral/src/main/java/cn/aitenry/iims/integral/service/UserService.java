package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.common.model.dto.BaseUserInfoQueryDTO;
import cn.aitenry.iims.common.model.entity.base.BaseUserInfo;
import cn.aitenry.iims.common.model.entity.integral.User;
import cn.aitenry.iims.common.model.vo.BaseUserInfoVO;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.integral.model.dto.user.UserDTO;
import cn.aitenry.iims.integral.model.dto.user.UserLoginDTO;
import cn.aitenry.iims.integral.model.dto.user.UserPageQueryDTO;
import cn.aitenry.iims.integral.model.dto.user.UserPasswordDTO;
import cn.aitenry.iims.integral.model.entity.PublicKey;
import cn.aitenry.iims.integral.model.vo.user.UserMenuVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public interface UserService {

    /**
     * 新增管理员
     * @param userRegisterDto UserDto
     */
    void save(UserDTO userRegisterDto);

    /**
     * 管理员登录
     * @param userLoginDto UserLoginDto
     * @return User
     */
    User login(UserLoginDTO userLoginDto);

    /**
     * 分页查询
     * @param userPageQueryDTO UserPageQueryDTO
     * @return PageResult
     */
    PageResult pageQuery(UserPageQueryDTO userPageQueryDTO);

    /**
     * 启用禁用管理员账号
     * @param isDisable 禁用状态
     * @param id 管理员ID
     */
    void startOrStop(Boolean isDisable, Long id);

    /**
     * 编辑管理员信息
     * @param userDto UserDto
     */
    void update(UserDTO userDto);

    /**
     * 根据id查询管理员信息
     * @param id 管理员ID
     * @return User
     */
    User getById(Long id);

    /**
     * 根据ID删除管理员
     * @param id 管理员ID
     */
    void deleteById(Long id);

    /**
     * 根据用户id查询菜单集合
     * @return List<UserMenuVo>
     */
    List<UserMenuVO> getMenusByUserId(Long id);

    /**
     * 修改密码
     * @param userPasswordDto UserPasswordDto
     */
    void updatePassword(UserPasswordDTO userPasswordDto);

    /**
     * 导出运营数据报表
     * @param response HttpServletResponse
     */
    void exportBusinessData(HttpServletResponse response);

    List<BaseUserInfo> baseInfoQuery(BaseUserInfoQueryDTO baseInfoQueryDto);

    BaseUserInfoVO getUserBaseInfoById(Long id);

    List<BaseUserInfoVO> getUserBaseInfoByIds(List<Long> ids);

    HashMap<Long, BaseUserInfoVO> getUserBaseHashInfoByIds(List<Long> ids);

    PublicKey getPublicKey();
}
