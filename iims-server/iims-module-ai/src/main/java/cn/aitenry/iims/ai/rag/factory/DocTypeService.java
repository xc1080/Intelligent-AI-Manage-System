package cn.aitenry.iims.ai.rag.factory;

import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public interface DocTypeService {

    DocumentTypeEnum getType();
    List<Document> getDocument(Long id);
    String getName(Long id);

}
