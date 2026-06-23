package cn.aitenry.iims.subscriber.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@Component
@ConfigurationProperties(prefix = "iims.subscriber.sse")
public class SseProperties {

    private final long timeout = 3600_000;

    private final long retry = 3000;

}