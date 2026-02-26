package cn.aitenry.iims.subscriber.service;

import cn.aitenry.iims.subscriber.model.entity.SseMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @Author: Aitenry
 * @Date: 2026/2/5 21:19
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface SseNotificationService {

    SseEmitter registerClient();

    /**
     * 全局广播消息（任意地方调用）
     */
    <T> void broadcast(SseMessage<T> message);

    /**
     * 定向推送（按 clientId）
     */
    <T> void sendToClient(Long clientId, SseMessage<T> message);

    void removeClient(Long clientId, String reason);

    /**
     * 获取当前活跃连接数（用于监控）
     */
    int getActiveConnectionCount();

}
