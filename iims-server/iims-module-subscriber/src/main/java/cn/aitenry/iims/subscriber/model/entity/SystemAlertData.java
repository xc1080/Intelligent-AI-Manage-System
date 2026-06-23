package cn.aitenry.iims.subscriber.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @Author: Aitenry
 * @Date: 2025/11/15 23:44
 * @Version: v1.0.0

 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAlertData {
    private String alertType;
    private Double currentValue;
    private Double threshold;
    private String resourceId;
    private LocalDateTime createTime;
}