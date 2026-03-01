package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Role;
import cn.aitenry.iims.integral.model.dto.role.RolePageQueryDTO;
import cn.aitenry.iims.integral.model.vo.role.RoleVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface RoleMapper {
    /**
     * 新增角色
     * @param role Role
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Role role);

    /**
     * 编辑角色
     * @param role Role
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Role role);

    /**
     * 角色分页查询
     * @param rolePageQueryDTO RolePageQueryDTO
     * @return Page<RoleVO>
     */
    Page<RoleVO> pageQuery(RolePageQueryDTO rolePageQueryDTO);

    /**
     * 根据id删除角色
     * @param id 角色ID
     */
    void deleteById(Long id);

    /**
     * 根据id查询角色
     * @param id 角色ID
     * @return Role
     */
    Role getById(Long id);

    /**
     * 查询所有角色
     * @return List<RoleVO>
     */
    List<RoleVO> list();
}
