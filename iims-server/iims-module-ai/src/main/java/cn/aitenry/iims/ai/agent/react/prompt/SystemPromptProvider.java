package cn.aitenry.iims.ai.agent.react.prompt;

import java.util.Map;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface SystemPromptProvider {
    String getSystemPrompt(Map<String, Object> context);
}
