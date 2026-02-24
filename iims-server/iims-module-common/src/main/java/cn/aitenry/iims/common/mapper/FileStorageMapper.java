package cn.aitenry.iims.common.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.file.FileStatus;
import cn.aitenry.iims.common.model.entity.file.FileWarehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileStorageMapper {

    @AutoFill(value = OperationType.INSERT)
    void insert(FileWarehouse fileWarehouse);

    @Select("select file_path from iims_integral_warehouse where id = #{id};")
    String getObjectNameById(Long id);

    @Select("SELECT * from iims_integral_warehouse WHERE file_key = #{fileKey}")
    FileWarehouse getFileInfoByFileKey(String fileKey);

    List<FileWarehouse> getObjectByIds(@Param("createBy") Long createBy, @Param("ids") List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(FileWarehouse fileWarehouse);

    @AutoFill(value = OperationType.UPDATE)
    void updateFileStatus(FileStatus fileStatus);

    @Select("select * from iims_integral_warehouse where file_status != -1 AND id = #{id};")
    FileWarehouse getObjectById(Long id);
}
