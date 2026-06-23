package cn.aitenry.iims.common.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.file.FileStatus;
import cn.aitenry.iims.common.model.entity.file.FileWarehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Mapper
public interface FileStorageMapper {

    @AutoFill(value = OperationType.INSERT)
    void insert(FileWarehouse fileWarehouse);

    String getFilePathById(Long id);

    FileWarehouse getFileInfoByKey(String fileKey);

    List<FileWarehouse> getFileInfoByIds(@Param("createBy") Long createBy, @Param("ids") List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(FileWarehouse fileWarehouse);

    @AutoFill(value = OperationType.UPDATE)
    void updateFileStatus(FileStatus fileStatus);

    FileWarehouse getFileInfoById(Long id);
}
