package cn.aitenry.iims.integral.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.aitenry.iims.integral.model.dto.menu.MenuDTO;
import cn.aitenry.iims.integral.model.dto.menu.MenuPageQueryDTO;
import cn.aitenry.iims.integral.model.vo.user.UserMenuVO;
import cn.aitenry.iims.integral.model.vo.menu.MenuVO;
import cn.aitenry.iims.integral.service.UserService;
import cn.aitenry.iims.integral.service.MenuService;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.common.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@RestController
@Slf4j
@RequestMapping("/iims/menu")
public class MenuController {

    private final MenuService menuService;
    private final UserService userService;

    public MenuController (MenuService menuService, UserService userService) {
        this.menuService = menuService;
        this.userService = userService;
    }

    /**
     * 新增菜单
     *
     * @param menuDto MenuDto
     * @return Result<String>
     */
    @PostMapping
    @ApiOperation("新增菜单")
    @SaCheckPermission("permission:menu:query")
    public Result<String> save(@RequestBody MenuDTO menuDto) {
        menuService.save(menuDto);
        return Result.success();
    }

    /**
     * 编辑菜单
     *
     * @param menuDto MenuDto
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("编辑菜单")
    @SaCheckPermission("permission:menu:update")
    public Result<String> update(@RequestBody MenuDTO menuDto) {
        menuService.update(menuDto);
        return Result.success();
    }

    /**
     * 菜单分页查询
     *
     * @param menuPageQueryDTO MenuPageQueryDTO
     * @return Result<PageResult>
     */
    @PostMapping("/page")
    @ApiOperation("菜单分页查询")
    @SaCheckPermission("permission:menu:query")
    public Result<PageResult> page(@RequestBody MenuPageQueryDTO menuPageQueryDTO) {
        PageResult pageResult = menuService.pageQuery(menuPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用停用菜单
     *
     * @param status Integer
     * @param id Long
     * @return Result<String>
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用停用菜单")
    @SaCheckPermission("permission:menu:update")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        menuService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id删除菜单
     *
     * @param id Long
     * @return Result<String>
     */
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除菜单")
    @SaCheckPermission("permission:menu:delete")
    public Result<String> deleteById(@PathVariable Long id) {
        menuService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id查询菜单
     *
     * @param id Long
     * @return Result<MenuVO>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜单")
    @SaCheckPermission("permission:menu:query")
    public Result<MenuVO> getById(@PathVariable Long id) {
        MenuVO menu = menuService.getById(id);
        return Result.success(menu);
    }

    /**
     * 查询所有菜单
     *
     * @return Result<List<MenuVO>>
     */
    @GetMapping("/list")
    @ApiOperation("查询所有菜单")
    @SaCheckPermission("permission:menu:query")
    public Result<List<MenuVO>> list() {
        List<MenuVO> list = menuService.list();
        return Result.success(list);
    }

    /**
     * 查询菜单树形结构（角色界面使用）
     *
     * @return Result<List<UserMenuVO>>
     */
    @GetMapping("/menuTree")
    @ApiOperation("查询所有菜单")
    public Result<List<UserMenuVO>> handleTree() {
        //查询当前用户的id
        long id = StpUtil.getLoginIdAsLong();
        List<UserMenuVO> menusByUserId = menuService.getTreeMenus(id);
        return Result.success(menusByUserId);
    }

    /**
     * 查询所有菜单
     *
     * @return Result<List<UserMenuVO>>
     */
    @GetMapping("/getMenuListByUserId")
    @ApiOperation("查询所有菜单")
    public Result<List<UserMenuVO>> getMenuListByUserId() {
        long id = StpUtil.getLoginIdAsLong();
        List<UserMenuVO> menusByUserId = userService.getMenusByUserId(id);
        return Result.success(menusByUserId);
    }
}
