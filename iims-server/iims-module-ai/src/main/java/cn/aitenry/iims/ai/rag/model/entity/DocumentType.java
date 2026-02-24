package cn.aitenry.iims.ai.rag.model.entity;

import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentType implements Serializable {

    private Long id;

    private DocumentTypeEnum type;

}
