package cn.aitenry.iims.common.service;

import cn.aitenry.iims.common.enums.FileStatusEnum;
import cn.aitenry.iims.common.model.entity.file.FileWarehouse;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface FileStorageService {

    List<FileWarehouse> getFileInfoByIds(List<Long> ids);

    FileWarehouse getFileInfoById(Long id);

    void update(FileWarehouse fileWarehouse);

    void changeFile(Long oldFileId, Integer changOldStatus,
                    Long newFileId, Integer changNewStatus);

    void updateFileStatus(Long fileId, FileStatusEnum status);

}
