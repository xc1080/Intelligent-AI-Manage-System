package cn.aitenry.iims.ai.rag.model.entity;

import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentType implements Serializable {

    private Long id;

    private DocumentTypeEnum type;

}
