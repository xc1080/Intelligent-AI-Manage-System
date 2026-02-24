package cn.aitenry.iims.common.service;

import cn.aitenry.iims.common.enums.FileStatusEnum;
import cn.aitenry.iims.common.model.entity.file.FileWarehouse;

import java.util.List;

public interface FileStorageService {

    List<FileWarehouse> getObjectByIds(List<Long> ids);

    FileWarehouse getObjectById(Long id);

    void update(FileWarehouse fileWarehouse);

    void changeFile(Long oldFileId, Integer changOldStatus,
                    Long newFileId, Integer changNewStatus);

    void updateFileStatus(Long fileId, FileStatusEnum status);

}
