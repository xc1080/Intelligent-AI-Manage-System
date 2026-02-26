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
 * @Description: TODO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActionData {
    private Long userId;
    private String title;
    private String content;
    private String actionType;
    private LocalDateTime createTime;
}