package cn.aitenry.iims.ai.agent.react.event;

import org.springframework.ai.chat.messages.Message;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public record LlmMessageEvent (Message message, Boolean isComplete) implements ReActAgentEvent {

    @Override
    public Boolean getComplete() {
        return isComplete;
    }

    @Override
    public Message getContent() {
        return message;
    }

}
