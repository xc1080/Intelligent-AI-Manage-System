package cn.aitenry.iims.ai.chat.model.entity;

import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: iims_aigc_topic 表对应的实体类
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiChatTopic extends BaseTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer top;

    private String title;

    private Boolean isDeleted;

}