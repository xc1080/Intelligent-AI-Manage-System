package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.base.BaseUserInfo;
import cn.aitenry.iims.common.model.entity.integral.User;
import cn.aitenry.iims.integral.model.dto.user.UserLoginDTO;
import cn.aitenry.iims.integral.model.dto.user.UserPageQueryDTO;
import cn.aitenry.iims.integral.model.vo.role.RoleVO;
import cn.aitenry.iims.integral.model.vo.user.UserMenuVO;
import cn.aitenry.iims.integral.model.vo.user.UserVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Mapper
public interface UserMapper {

    /**
     * 新增用户
     *
     * @param user User
     */
    @AutoFill(value = OperationType.INSERT)
    int insert(User user);

    /**
     * 根据用户名或者邮箱查询用户
     *
     * @return User
     */
    User getByUserNameOrEmail(UserLoginDTO userLoginDto);

    /**
     * 分页查询
     *
     * @param userPageQueryDTO UserPageQueryDTO
     * @return Page<UserVO>
     */
    Page<UserVO> pageQuery(UserPageQueryDTO userPageQueryDTO);

    /**
     *根据主键动态修改属性
     * @param user User
     */
    @AutoFill(value = OperationType.UPDATE)
    int update(User user);

    /**
     * 根据id查询用户信息
     * @param id 用户ID
     * @return User
     */
    User selectObjectById(Long id);

    List<User> selectObjectByIds(List<Long> ids);


    Long getAvatarById(Long id);

    /**
     * 根据用户id查询权限集合
     * @param userId 用户ID
     * @return Set<String>
     */
    Set<String> searchUserPermissions(String userId);

    /**
     * 根据用户id查询角色集合
     * @param userId 用户ID
     * @return List<RoleVO>
     */
    List<RoleVO> searchUserRoles(String userId);

    /**
     * 根据用户id查询菜单集合
     * @param userId 用户ID
     * @return List<UserMenuVo>
     */
    List<UserMenuVO> getMenusByUserId(Long userId);

    /**
     * 根据用户ID获取用户所拥有的角色名称
     * @param ids 用户ID
     * @return List<String>
     */
    List<String> getRoleNameByIds(List<String> ids);

    /**
     * 查询所有用户
     * @return List<User>
     */
    List<User> list();

    List<BaseUserInfo> baseInfoQuery(String username);
}
