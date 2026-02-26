package cn.aitenry.iims.subscriber.service.impl;

import cn.aitenry.iims.common.context.BaseContext;
import cn.aitenry.iims.subscriber.config.SseProperties;
import cn.aitenry.iims.subscriber.model.entity.SseMessage;
import cn.aitenry.iims.subscriber.service.SseNotificationService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SseNotificationServiceImpl implements SseNotificationService {

    private final SseProperties sseProperties;
    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    @Override
    public SseEmitter registerClient() {
        Long clientId = BaseContext.getCurrentId();
        SseEmitter emitter = new SseEmitter(sseProperties.getTimeout());

        emitter.onCompletion(() -> removeClient(clientId, "completed"));
        emitter.onTimeout(() -> removeClient(clientId, "timeout"));
        emitter.onError(ex -> removeClient(clientId, "error: " + ex.getMessage()));

        clients.put(clientId, emitter);
        log.info("SSE客户端注册: {}, 当前连接数: {}", clientId, clients.size());
        return emitter;
    }

    @Override
    public <T> void broadcast(SseMessage<T> message) {
        sendToClients(new CopyOnWriteArrayList<>(clients.keySet()), message);
    }

    @Override
    public <T> void sendToClient(Long clientId, SseMessage<T> message) {
        sendToClients(Collections.singletonList(clientId), message);
    }

    @Override
    public int getActiveConnectionCount() {
        return clients.size();
    }

    @PreDestroy
    public void shutdown() {
        log.info("SSE服务关闭，清理 {} 个连接", clients.size());
        clients.keySet().forEach(id -> removeClient(id, "shutdown"));
    }

    private <T> void sendToClients(List<Long> targetIds, SseMessage<T> msg) {
        if (targetIds.isEmpty() || msg == null) return;

        targetIds.forEach(clientId -> {
            SseEmitter emitter = clients.get(clientId);
            if (emitter == null) return;

            try {
                // Jackson 会处理泛型序列化
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(msg.getId()))
                        .name(msg.getEvent())
                        .data(msg.getData())  // T 类型自动序列化
                        .reconnectTime(msg.getRetry() != null ? msg.getRetry() : sseProperties.getRetry()));
            } catch (IOException e) {
                log.warn("SSE消息发送失败 [clientId={}]: {}", clientId, e.getMessage());
                removeClient(clientId, "send-failed");
            }
        });
    }

    @Override
    public void removeClient(Long clientId, String reason) {
        SseEmitter emitter = clients.remove(clientId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (IllegalStateException ex) {
                log.warn("移除SSE失败 [clientId={}]: {}", clientId, ex.getMessage());
            }
            log.info("SSE客户端移除 [{}]: {}, 剩余连接数: {}", reason, clientId, clients.size());
        }
    }
}