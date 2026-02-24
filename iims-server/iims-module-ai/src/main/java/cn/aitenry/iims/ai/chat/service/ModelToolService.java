package cn.aitenry.iims.ai.chat.service;

import cn.aitenry.iims.ai.rag.enums.DomProcessEnum;
import cn.aitenry.iims.ai.rag.model.entity.DocumentType;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface ModelToolService {

    String imageOcrByModel(List<Long> fileIds);

    String generateSummaryByModel(DocumentType documentType);

    void documentEmbeddingByModel(Long wikiId, DomProcessEnum domProcess);

}
