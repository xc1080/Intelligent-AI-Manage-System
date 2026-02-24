package cn.aitenry.iims.archive.mapper;

import com.github.pagehelper.Page;
import cn.aitenry.iims.archive.model.dto.ArchiveMenuPageQueryDTO;
import cn.aitenry.iims.archive.model.entity.ArchiveBaseMetadata;
import cn.aitenry.iims.archive.model.entity.ArchiveMetadata;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import cn.aitenry.iims.archive.model.vo.ArchiveMetadataVO;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArchiveMetadataMapper {

    Page<ArchiveBaseMetadata> pageQuery(ArchiveMenuPageQueryDTO archiveMenuPageQueryDto);

    @Select("SELECT * FROM iims_archive_metadata WHERE id = #{id}")
    ArchiveMetadataVO getArchiveMetadataById(Long id);

    /**
     *根据主键动态修改属性
     * @param metadata ArchiveMetadata
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(ArchiveMetadata metadata);

    /**
     *添加档案属性
     * @param metadata ArchiveMetadata
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(ArchiveMetadata metadata);

    /**
     * 批量更新档案删除状态
     * @param deletedStatus ArchiveStatus
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateArchiveDeleted(DeletedStatus deletedStatus);
}
