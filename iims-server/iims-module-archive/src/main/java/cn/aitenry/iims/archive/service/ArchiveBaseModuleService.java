package cn.aitenry.iims.archive.service;

import cn.aitenry.iims.archive.model.dto.AddArchiveMetadataDTO;
import cn.aitenry.iims.archive.model.dto.ArchiveMenuPageQueryDTO;
import cn.aitenry.iims.archive.model.dto.EditArchiveMetadataDTO;
import cn.aitenry.iims.archive.model.vo.ArchiveMenuVO;
import cn.aitenry.iims.archive.model.vo.ArchiveMetadataVO;
import cn.aitenry.iims.common.result.PageResult;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 档案基础模块层
 **/
public interface ArchiveBaseModuleService {

    List<ArchiveMenuVO> getTreeMenus();

    PageResult pageQueryBaseMetadata(ArchiveMenuPageQueryDTO archiveMenuPageQueryDto);

    ArchiveMetadataVO getArchiveMetadataById(Long id);

    void editArchiveMetadata(EditArchiveMetadataDTO editArchiveMetadataDto);

    void addArchiveMetadata(AddArchiveMetadataDTO addArchiveMetadataDto);

    void delArchiveMetadata(List<Long> ids);
}
