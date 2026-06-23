package cn.aitenry.iims.ai.chat.model.vo;

import cn.aitenry.iims.ai.rag.model.entity.Metadata;
import cn.aitenry.iims.ai.rag.model.entity.MetadataScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocMetadataVO implements Serializable {

    private List<MetadataScore> score;

    private Metadata metadata;

}
