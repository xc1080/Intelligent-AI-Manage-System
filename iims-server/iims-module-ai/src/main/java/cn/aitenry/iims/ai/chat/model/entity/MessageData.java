package cn.aitenry.iims.ai.chat.model.entity;

import cn.aitenry.iims.common.enums.AiApiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageData implements Serializable {

    private Long topicId;

    private Long lastId;

    private List<Long> fileIds;

    private Long modelId;

    private AiApiType apiType;

    private List<Long> wikiIds;

    private String question;

    private SseEmitter sse;

    private List<AiContent> aiContent;

    private List<Document> documents;

    private List<ChatTool> tools;

}
