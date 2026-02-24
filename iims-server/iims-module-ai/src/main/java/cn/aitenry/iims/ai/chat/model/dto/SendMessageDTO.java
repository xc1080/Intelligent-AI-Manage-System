package cn.aitenry.iims.ai.chat.model.dto;

import cn.aitenry.iims.common.enums.AiApiType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
public class SendMessageDTO implements Serializable {

    private Long topicId;

    private Long lastId;

    private Long modelId;

    private AiApiType apiType;

    private List<Long> wikiIds;

    private List<Long> fileIds;

    private String question;

}
