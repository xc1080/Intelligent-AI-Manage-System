package cn.aitenry.iims.ai.chat.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Configuration
@EnableAutoConfiguration()
@EnableConfigurationProperties()
public class ModelsConfig {

    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(OllamaEmbeddingModel ollamaEmbeddingModel) {
        return ollamaEmbeddingModel;
    }

}