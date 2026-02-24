package cn.aitenry.iims.common.service;

import cn.aitenry.iims.common.model.entity.file.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface MinioService {

    FileUpload uploadFile(MultipartFile file, Integer itemType) throws Exception;

    String getPreviewUrl(Long fileId);

    String generateShortLink(Long fileId);

    String getBase64EncodedFile(String objectName);

    InputStream getInputStream(Long fileId);

}
