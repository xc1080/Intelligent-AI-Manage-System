package cn.aitenry.iims.ai.chat.model.entity;

import cn.aitenry.iims.common.enums.AiApiType;
import cn.aitenry.iims.common.enums.AiModelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatApi {

    private String key;

    private String url;

    private Integer token;

    private String name;

    private AiApiType type;

    private AiModelType modelType;

}
