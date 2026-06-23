package cn.aitenry.iims.ai.agent.react.message;

import org.springframework.ai.chat.messages.Message;

import java.util.Map;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public record BranchMessageItem(Message message, String id, String previousId, Map<String, Object> metadata) {
}