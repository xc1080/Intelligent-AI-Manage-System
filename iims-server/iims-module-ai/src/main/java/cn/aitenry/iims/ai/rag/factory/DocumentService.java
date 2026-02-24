package cn.aitenry.iims.ai.rag.factory;

import cn.aitenry.iims.ai.rag.enums.DomProcessEnum;

public interface DocumentService {
    DomProcessEnum getType();
    Boolean process(Long wikiId);
}