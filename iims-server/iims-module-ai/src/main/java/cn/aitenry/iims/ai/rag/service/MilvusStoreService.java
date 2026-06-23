package cn.aitenry.iims.ai.rag.service;

import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public interface MilvusStoreService {

    List<Document> loadDocumentByWikiReader(List<Long> wikiIds, String question, Integer topK);

    Boolean addDocumentByWiki(Long wikiId);

    void delDocumentByWiki(Long wikiId);

    void delDocumentByDocId(Long docId, DocumentTypeEnum type);

}
