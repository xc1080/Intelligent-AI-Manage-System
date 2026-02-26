package cn.aitenry.iims.subscriber.model.entity;

import cn.aitenry.iims.subscriber.enums.SseEventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 类型安全的 SSE 消息载体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SseMessage<T> implements Serializable {
    private Long id;
    private String event;
    private T data;
    private Long retry;
    private Long timestamp;

    /**
     * 私有构造器：强制通过静态工厂方法创建
     */
    private SseMessage(Long id, SseEventTypeEnum eventType, T data) {
        this.id = id;
        this.event = eventType.getEventName();
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.retry = null;

        eventType.validateDataType(data);
    }

    public static SseMessage<NotificationData> notification(Long id, NotificationData data) {
        return new SseMessage<>(id, SseEventTypeEnum.NOTIFICATION, data);
    }

    public static SseMessage<SystemAlertData> systemAlert(Long id, SystemAlertData data) {
        return new SseMessage<>(id, SseEventTypeEnum.SYSTEM_ALERT, data);
    }

    public static SseMessage<UserActionData> userAction(Long id, UserActionData data) {
        return new SseMessage<>(id, SseEventTypeEnum.USER_ACTION, data);
    }
}