package cn.aitenry.iims.ai.agent.react.prompt;

import java.util.Map;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public class FixedSystemPromptProvider implements SystemPromptProvider {

    private final String systemPrompt;

    public FixedSystemPromptProvider(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    @Override
    public String getSystemPrompt(Map<String, Object> context) {
        return systemPrompt;
    }
}
