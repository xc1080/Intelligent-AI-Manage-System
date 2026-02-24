package cn.aitenry.iims.ai.chat.service;

import cn.aitenry.iims.ai.chat.model.entity.ModelChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface ModelService {

    ChatModel getOllamaChatModel(OllamaApi ollamaApi, OllamaChatOptions ollamaOptions);

    ChatModel getOpenAIChatModel(OpenAiApi openAiApi, OpenAiChatOptions openAiOptions);

    ChatModel getDeepSeekChatModel(DeepSeekApi deepSeekApi, DeepSeekChatOptions deepSeekChatOptions);

    ChatModel getChatModel(Long modelId, ModelChatOptions options);

    default ChatModel getChatModel(Long modelId) {
        return getChatModel(modelId, ModelChatOptions.builder().build());
    }

    EmbeddingModel getOllamaEmbeddingModel(OllamaApi ollamaApi, OllamaEmbeddingOptions ollamaOptions);

    EmbeddingModel getOpenAiEmbeddingModel(OpenAiApi openAiApi, MetadataMode metadataMode, OpenAiEmbeddingOptions openAiOptions);

    EmbeddingModel getEmbeddingModel(Long modelId, MetadataMode metadataMode, ModelChatOptions options);

    default EmbeddingModel getEmbeddingModel(Long modelId) {
        return getEmbeddingModel(modelId, MetadataMode.ALL, ModelChatOptions.builder().build());
    }

}
