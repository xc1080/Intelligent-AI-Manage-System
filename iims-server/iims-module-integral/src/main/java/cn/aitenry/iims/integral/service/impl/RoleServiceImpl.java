package cn.aitenry.iims.integral.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.aitenry.iims.integral.mapper.RoleMapper;
import cn.aitenry.iims.integral.model.dto.role.RoleDTO;
import cn.aitenry.iims.integral.model.dto.role.RolePageQueryDTO;
import cn.aitenry.iims.integral.model.vo.role.RoleVO;
import cn.aitenry.iims.integral.service.RoleService;
import cn.aitenry.iims.common.constant.MessageConstant;
import cn.aitenry.iims.common.exception.DeletionNotAllowedException;
import cn.aitenry.iims.common.exception.NotFoundException;
import cn.aitenry.iims.common.model.entity.integral.Role;
import cn.aitenry.iims.common.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(RoleDTO roleDto) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDto, role);
        //新增的标签默认设置启用状态
//        role.setSystemic(StatusConstant.ENABLE);
        roleMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleDTO roleDto) {
        roleMapper.getById(roleDto.getId());
        Role role = new Role();
        BeanUtils.copyProperties(roleDto, role);
        roleMapper.update(role);
    }

    @Override
    public PageResult pageQuery(RolePageQueryDTO rolePageQueryDTO) {
        int page = rolePageQueryDTO.getPage();
        int pageSize = rolePageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        // select * from tags limit 0, 10
        Page<RoleVO> roles = roleMapper.pageQuery(rolePageQueryDTO);
        return new PageResult(roles.getTotal(), roles.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        Role role = roleMapper.getById(id);
        if (role == null) throw new NotFoundException(MessageConstant.DATA_NOT_FOUND);
        if (role.getSystemic() == 1) throw new DeletionNotAllowedException(MessageConstant.NOT_DELETE_SYSTEM_DATA);
        roleMapper.deleteById(id);
    }

    @Override
    public RoleVO getById(Long id) {
        Role role = roleMapper.getById(id);
        if (role == null) throw new NotFoundException(MessageConstant.DATA_NOT_FOUND);
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        return roleVO;
    }

    @Override
    public List<RoleVO> list() {
        return roleMapper.list();
    }
}
