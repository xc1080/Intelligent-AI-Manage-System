package cn.aitenry.iims.ai.rag.service.impl;

import cn.aitenry.iims.ai.chat.model.entity.ModelSetting;
import cn.aitenry.iims.ai.chat.service.AiChatSettingService;
import cn.aitenry.iims.ai.chat.service.ModelService;
import cn.aitenry.iims.ai.rag.service.CustomizeVectorStoreService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.stereotype.Service;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Slf4j
@Service
public class CustomizeVectorStoreServiceImpl implements CustomizeVectorStoreService {

    private final ModelService modelService;

    private final AiChatSettingService aiChatSettingService;

    public CustomizeVectorStoreServiceImpl(ModelService modelService, AiChatSettingService aiChatSettingService) {
        this.modelService = modelService;
        this.aiChatSettingService = aiChatSettingService;
    }

    @Override
    public VectorStore loadMilvusVectorStore(String dbName, String collectionName) {
        ModelSetting userModelSetting = aiChatSettingService.getUserModelSetting();
        EmbeddingModel embeddingModel = modelService.getEmbeddingModel(userModelSetting.getEmbeddingModel());
        MilvusServiceClient milvusServiceClient = new MilvusServiceClient(ConnectParam.newBuilder().build());
        return MilvusVectorStore.builder(milvusServiceClient, embeddingModel)
                .databaseName(dbName)
                .collectionName(collectionName)
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.COSINE)
                .iDFieldName("id")
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
    }

}
