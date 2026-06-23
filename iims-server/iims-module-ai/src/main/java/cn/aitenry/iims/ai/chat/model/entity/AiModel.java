package cn.aitenry.iims.ai.chat.model.entity;

import cn.aitenry.iims.common.enums.AiApiType;
import cn.aitenry.iims.common.enums.AiModelType;
import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Aitenry
 * @Date: 2026/2/14 16:09
 * @Version: v1.0.0

 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiModel extends BaseTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String rename;

    private AiApiType type;

    private AiModelType modelType;

    private String url;

    private String key;

    private String token;

    private String description;

    private Boolean isOnline;

    private Boolean isDeleted;

    private LocalDateTime detectionTime;

}
