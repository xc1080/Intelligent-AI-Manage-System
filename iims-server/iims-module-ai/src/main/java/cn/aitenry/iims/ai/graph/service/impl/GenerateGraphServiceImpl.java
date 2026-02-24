package cn.aitenry.iims.ai.graph.service.impl;

import com.alibaba.fastjson.JSONArray;
import cn.aitenry.iims.ai.graph.context.PromptTemplateContext;
import cn.aitenry.iims.ai.graph.model.entity.GraphEntity;
import cn.aitenry.iims.ai.graph.model.entity.GraphRelationship;
import cn.aitenry.iims.ai.chat.model.entity.ModelSetting;
import cn.aitenry.iims.ai.chat.service.AiChatSettingService;
import cn.aitenry.iims.ai.chat.service.ModelService;
import cn.aitenry.iims.ai.rag.utils.PromptTemplateUtil;
import cn.aitenry.iims.ai.graph.service.GenerateGraphService;
import cn.aitenry.iims.common.utils.SnowFlakeIdWorker;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class GenerateGraphServiceImpl implements GenerateGraphService {

    private final SnowFlakeIdWorker aiSnowFlakeIdWorker;
    private final ModelService modelService;
    private final AiChatSettingService settingService;

    public GenerateGraphServiceImpl(SnowFlakeIdWorker aiSnowFlakeIdWorker, ModelService modelService,
                                    AiChatSettingService settingService) {
        this.aiSnowFlakeIdWorker = aiSnowFlakeIdWorker;
        this.modelService = modelService;
        this.settingService = settingService;
    }


    public long uniqueId() {
        return aiSnowFlakeIdWorker.nextId();
    }


    @Override
    public List<GraphEntity> extractEntityByModel(String chunk) {
        SystemMessage systemMessage = new SystemMessage(PromptTemplateContext.EXTRACT_ENTITY_SYSTEM);
        Message userMessage = new PromptTemplate(PromptTemplateContext.EXTRACT_ENTITY_USER).createMessage(Map.of("chunk", chunk));
        List<Message> messages = new ArrayList<>(List.of(systemMessage, userMessage));

        ModelSetting modelSetting = settingService.getUserModelSetting();
        ChatResponse call = modelService.getChatModel(modelSetting.getModelId())
                .call(new Prompt(messages));
        String result = PromptTemplateUtil.removeJsonCodeBlocks(PromptTemplateUtil.removeThink(call.getResult().getOutput().getText()));
        List<GraphEntity> graphEntities = JSONArray.parseArray(result, GraphEntity.class);
        graphEntities.forEach(entity -> entity.setId(uniqueId()));
        return graphEntities;
    }

    @Override
    public List<GraphRelationship> generateRelationshipByModel(String chunk, String content) {
        SystemMessage systemMessage = new SystemMessage(PromptTemplateContext.GENERATE_RELATIONSHIP_SYSTEM);
        Message userMessage = new PromptTemplate(PromptTemplateContext.GENERATE_RELATIONSHIP_USER)
                .createMessage(Map.of("chunk", chunk, "content", content));
        List<Message> messages = new ArrayList<>(List.of(systemMessage, userMessage));
        ModelSetting modelSetting = settingService.getUserModelSetting();
        ChatResponse call = modelService.getChatModel(modelSetting.getModelId()).call(new Prompt(messages));
        String result = PromptTemplateUtil.removeJsonCodeBlocks(PromptTemplateUtil.removeThink(call.getResult().getOutput().getText()));
        List<GraphRelationship> graphRelationships = JSONArray.parseArray(result, GraphRelationship.class);
        graphRelationships.forEach(relationship -> relationship.setId(uniqueId()));
        return graphRelationships;
    }

}
