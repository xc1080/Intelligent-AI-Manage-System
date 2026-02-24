package cn.aitenry.iims.subscriber.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SseMessage implements Serializable {
    private String id;
    private String event;
    private Object data;
    private Long retry;
    private Long timestamp;

    public SseMessage(String event, Object data) {
        this.event = event;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.id = java.util.UUID.randomUUID().toString();
    }
}