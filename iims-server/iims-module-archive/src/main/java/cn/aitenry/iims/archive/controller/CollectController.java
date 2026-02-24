package cn.aitenry.iims.archive.controller;

import cn.aitenry.iims.archive.model.dto.AddArchiveMetadataDTO;
import cn.aitenry.iims.archive.model.dto.ArchiveMenuPageQueryDTO;
import cn.aitenry.iims.archive.model.dto.EditArchiveMetadataDTO;
import cn.aitenry.iims.archive.model.vo.ArchiveMenuVO;
import cn.aitenry.iims.archive.model.vo.ArchiveMetadataVO;
import cn.aitenry.iims.archive.service.ArchiveBaseModuleService;
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
@RequestMapping("/iims/dms/collect")
public class CollectController {

    private final ArchiveBaseModuleService archiveBaseModuleService;

    public CollectController(ArchiveBaseModuleService archiveBaseModuleService) {
        this.archiveBaseModuleService = archiveBaseModuleService;
    }

    @GetMapping("/menuTree")
    @ApiOperation("查询所有档案树性类目菜单")
    public Result<List<ArchiveMenuVO>> handleTree() {
        List<ArchiveMenuVO> menusByUserId = archiveBaseModuleService.getTreeMenus();
        return Result.success(menusByUserId);
    }

    @PostMapping("/page")
    @ApiOperation("档案树分页查询")
    public Result<PageResult> page(@RequestBody ArchiveMenuPageQueryDTO archiveMenuPageQueryDto) {
        PageResult pageResult = archiveBaseModuleService.pageQueryBaseMetadata(archiveMenuPageQueryDto);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询档案信息
     *
     * @param id 档案ID
     * @return Result<ArchiveMetadataVo>
     */
    @GetMapping("/metadata/{id}")
    @ApiOperation("根据id查询档案信息")
    public Result<ArchiveMetadataVO> getArchiveMetadataById(@PathVariable Long id) {
        ArchiveMetadataVO metadata = archiveBaseModuleService.getArchiveMetadataById(id);
        return Result.success(metadata);
    }

    @PostMapping("/edit/metadata")
    @ApiOperation("编辑当前档案信息")
    public Result<String> editMetadata(@RequestBody EditArchiveMetadataDTO editArchiveMetadataDto) {
        archiveBaseModuleService.editArchiveMetadata(editArchiveMetadataDto);
        return Result.success();
    }

    @PostMapping("/add/metadata")
    @ApiOperation("新增档案")
    public Result<String> addMetadata(@RequestBody AddArchiveMetadataDTO addArchiveMetadataDto) {
        archiveBaseModuleService.addArchiveMetadata(addArchiveMetadataDto);
        return Result.success();
    }

    @PostMapping("/del/metadata")
    @ApiOperation("删除档案")
    public Result<String> delMetadata(@RequestBody List<Long> ids) {
        archiveBaseModuleService.delArchiveMetadata(ids);
        return Result.success();
    }

}
