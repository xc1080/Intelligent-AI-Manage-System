package cn.aitenry.iims.ai.rag.factory;

import cn.aitenry.iims.ai.rag.enums.DomProcessEnum;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface DocumentService {
    DomProcessEnum getType();
    Boolean process(Long wikiId);
}