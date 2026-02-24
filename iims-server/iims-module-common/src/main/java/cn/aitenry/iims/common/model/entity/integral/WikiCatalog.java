package cn.aitenry.iims.common.model.entity.integral;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

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
@EqualsAndHashCode(callSuper = true)
public class WikiCatalog extends BaseTable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long wikiId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long docId;

    private DocumentTypeEnum type;

    private String title;

    private Integer level;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long parentId;

    private Integer sort;

    private Boolean isDeleted;

    private Boolean isEmbedding;

}
