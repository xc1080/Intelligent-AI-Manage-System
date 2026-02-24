package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.integral.model.dto.role.RoleDTO;
import cn.aitenry.iims.integral.model.dto.role.RolePageQueryDTO;
import cn.aitenry.iims.integral.model.vo.role.RoleVO;
import cn.aitenry.iims.common.result.PageResult;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface RoleService {
    /**
     * 新增角色
     * @param roleDto RoleDto
     */
    void save(RoleDTO roleDto);

    /**
     * 编辑角色
     * @param roleDto RoleDto
     */
    void update(RoleDTO roleDto);

    /**
     * 角色分页查询
     * @param rolePageQueryDTO RolePageQueryDTO
     * @return PageResult
     */
    PageResult pageQuery(RolePageQueryDTO rolePageQueryDTO);

    /**
     * 根据ID删除角色
     * @param id 角色ID
     */
    void deleteById(Long id);

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return RoleVO
     */
    RoleVO getById(Long id);

    /**
     * 查询所有角色
     * @return List<RoleVO>
     */
    List<RoleVO> list();
}
