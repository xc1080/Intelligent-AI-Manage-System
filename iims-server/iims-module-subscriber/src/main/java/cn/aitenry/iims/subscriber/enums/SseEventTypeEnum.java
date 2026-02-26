package cn.aitenry.iims.subscriber.enums;

import cn.aitenry.iims.subscriber.model.entity.NotificationData;
import cn.aitenry.iims.subscriber.model.entity.SystemAlertData;
import cn.aitenry.iims.subscriber.model.entity.UserActionData;
import lombok.Getter;

/**
 * SSE 事件类型枚举
 * @Author: Aitenry
 * @Date: 2025/11/15 23:44
 * @Version: v1.0.0
 * @Description: （含数据类型约束）
 **/
@Getter
public enum SseEventTypeEnum {
    NOTIFICATION("NOTIFICATION", NotificationData.class),
    SYSTEM_ALERT("SYSTEM_ALERT", SystemAlertData.class),
    USER_ACTION("USER_ACTION", UserActionData.class);

    private final String eventName;
    private final Class<?> dataType;

    SseEventTypeEnum(String eventName, Class<?> dataType) {
        this.eventName = eventName;
        this.dataType = dataType;
    }

    /**
     * 验证数据类型是否匹配
     */
    public void validateDataType(Object data) {
        if (data == null) {
            throw new IllegalArgumentException(
                String.format("事件 [%s] 不允许 null 数据", eventName)
            );
        }
        if (!dataType.isInstance(data)) {
            throw new IllegalArgumentException(
                String.format("事件 [%s] 要求数据类型为 %s，实际为 %s",
                    eventName, dataType.getSimpleName(), data.getClass().getSimpleName())
            );
        }
    }

    public static SseEventTypeEnum fromName(String name) {
        for (SseEventTypeEnum type : values()) {
            if (type.eventName.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的 SSE 事件类型: " + name);
    }
}