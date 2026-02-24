package cn.aitenry.iims.ai.rag.factory;

import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import org.springframework.ai.document.Document;

import java.util.List;

public interface DocTypeService {

    DocumentTypeEnum getType();
    List<Document> getDocument(Long id);
    String getName(Long id);

}
