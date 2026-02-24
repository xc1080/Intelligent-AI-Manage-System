package cn.aitenry.iims.ai.rag.service;

import org.springframework.ai.vectorstore.VectorStore;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface CustomizeVectorStoreService {

    VectorStore loadMilvusVectorStore(String dbName, String collectionName);

}
