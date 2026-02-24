package cn.aitenry.iims.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@Component
@ConfigurationProperties(prefix = "iims.setting.comment")
public class CommentProperties {

    private Boolean examine;

    private Boolean sensitive;

}