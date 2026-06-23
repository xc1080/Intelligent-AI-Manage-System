package cn.aitenry.iims.integral.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.aitenry.iims.integral.mapper.MenuMapper;
import cn.aitenry.iims.integral.model.dto.menu.MenuDTO;
import cn.aitenry.iims.integral.model.dto.menu.MenuPageQueryDTO;
import cn.aitenry.iims.integral.model.vo.user.UserMenuVO;
import cn.aitenry.iims.integral.model.vo.menu.MenuVO;
import cn.aitenry.iims.integral.service.MenuService;
import cn.aitenry.iims.common.constant.MessageConstant;
import cn.aitenry.iims.common.constant.StatusConstant;
import cn.aitenry.iims.common.exception.FieldCannotEmptyException;
import cn.aitenry.iims.common.exception.NotFoundException;
import cn.aitenry.iims.common.model.entity.integral.Menu;
import cn.aitenry.iims.common.result.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(MenuDTO menuDto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuDto, menu);
        //新增的菜单默认设置启用状态
        menu.setStatus(StatusConstant.ENABLE);
        menuMapper.insert(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MenuDTO menuDto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuDto, menu);
        if (menu.getId() == null) throw new FieldCannotEmptyException("id " + MessageConstant.FIELD_IS_NOT_NULL);
        menuMapper.update(menu);
    }

    @Override
    public PageResult pageQuery(MenuPageQueryDTO menuPageQueryDTO) {
        int page = menuPageQueryDTO.getPage();
        int pageSize = menuPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        // select * from menu limit 0, 10
        Page<MenuVO> menu = menuMapper.pageQuery(menuPageQueryDTO);
        return new PageResult(menu.getTotal(), menu.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(Integer status, Long id) {
        if (id == null) throw new FieldCannotEmptyException("id " + MessageConstant.FIELD_IS_NOT_NULL);
        Menu menu = new Menu();
        menu.setId(id);
        menu.setStatus(status);
        menuMapper.update(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        Menu menu = menuMapper.getById(id);
        if (menu == null) throw new NotFoundException(MessageConstant.DATA_NOT_FOUND);
        menuMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuVO getById(Long id) {
        Menu menu = menuMapper.getById(id);
        if (menu == null) throw new NotFoundException(MessageConstant.DATA_NOT_FOUND);
        MenuVO menuVO = new MenuVO();
        BeanUtils.copyProperties(menu, menuVO);
        return menuVO;
    }

    @Override
    public List<MenuVO> list() {
        return menuMapper.all();
    }

    @Override
    public List<UserMenuVO> getTreeMenus(Long userId) {
        List<UserMenuVO> allMenus = menuMapper.allMenu(); // 获取所有菜单的方法
        Map<Long, UserMenuVO> menuMap = new HashMap<>(); // 用于快速查找菜单项

        // 将所有菜单放入Map中，便于后续快速定位
        for (UserMenuVO menu : allMenus) {
            menuMap.put(menu.getId(), menu);
        }

        // 从所有菜单中筛选出根节点（父ID为0）
        List<UserMenuVO> rootList = new ArrayList<>();
        for (UserMenuVO menu : allMenus) {
            if (menu.getParentId().equals(0L)) {
                rootList.add(menu);
                // 递归构建子菜单
                findChild(menu, menuMap);
            }
        }
        return rootList;
    }

    /**
     * 递归查找子菜单
     * @param menu 当前菜单项
     * @param menuMap 所有菜单的Map映射，用于快速定位
     */
    private void findChild(UserMenuVO menu, Map<Long, UserMenuVO> menuMap) {
        List<UserMenuVO> children = new ArrayList<>();
        for (UserMenuVO child : menuMap.values()) {
            if (child.getParentId().equals(menu.getId())) {
                children.add(child);
                // 继续查找子菜单的子菜单
                findChild(child, menuMap);
            }
        }
        menu.setChildren(children);
    }
}
