package cn.aitenry.iims.ai.chat.model.entity;

import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ModelSetting extends BaseTable {

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 嵌入向量模型ID
     */
    private Long embeddingModel;

}