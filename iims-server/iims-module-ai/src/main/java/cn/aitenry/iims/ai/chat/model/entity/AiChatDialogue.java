package cn.aitenry.iims.ai.chat.model.entity;

import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
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
@EqualsAndHashCode(callSuper = true)
public class AiChatDialogue extends BaseTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long lastId;

    private Long topicId;

    private String sender;

    private String content;

    private String metadata;

    private String tools;

    private List<Long> fileIds;

    private Boolean isDeleted;

    private Integer feedbackStatus;

    private Boolean isStar;

}
