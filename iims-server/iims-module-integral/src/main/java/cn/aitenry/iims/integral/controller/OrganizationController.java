package cn.aitenry.iims.integral.controller;

import cn.aitenry.iims.common.result.Result;
import cn.aitenry.iims.integral.model.dto.organization.OrganizationDTO;
import cn.aitenry.iims.integral.model.vo.organization.OrganizationMenuVO;
import cn.aitenry.iims.integral.model.vo.organization.OrganizationVO;
import cn.aitenry.iims.integral.service.OrganizationService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/15 23:58
 * @Version: v1.0.0

 **/
@RestController
@Slf4j
@RequestMapping("/iims/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/menuTree")
    @ApiOperation("查询所有菜单")
    public Result<List<OrganizationMenuVO>> handleTree(@RequestParam(required = false) Long companyId) {
        List<OrganizationMenuVO> treeMenus = organizationService.getTreeMenus(companyId);
        return Result.success(treeMenus);
    }

    @GetMapping("/allMenuTree")
    @ApiOperation("查询所有菜单")
    public Result<List<OrganizationVO>> handleAllTree() {
        List<OrganizationVO> treeMenus = organizationService.getAllMenuList();
        return Result.success(treeMenus);
    }

    /**
     * 根据id删除菜单
     *
     * @param id Long
     * @return Result<String>
     */
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除组织")
    public Result<String> deleteById(@PathVariable Long id) {
        organizationService.deleteById(id);
        return Result.success();
    }

    /**
     * 新增菜单
     *
     * @param organizationDTO OrganizationDTO
     * @return Result<String>
     */
    @PostMapping
    @ApiOperation("新增菜单")
    @SaCheckPermission("permission:menu:query")
    public Result<String> save(@RequestBody OrganizationDTO organizationDTO) {
        organizationService.save(organizationDTO);
        return Result.success();
    }

    /**
     * 编辑菜单
     *
     * @param organizationDTO OrganizationDTO
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("编辑菜单")
    @SaCheckPermission("permission:menu:update")
    public Result<String> update(@RequestBody OrganizationDTO organizationDTO) {
        organizationService.update(organizationDTO);
        return Result.success();
    }

}
