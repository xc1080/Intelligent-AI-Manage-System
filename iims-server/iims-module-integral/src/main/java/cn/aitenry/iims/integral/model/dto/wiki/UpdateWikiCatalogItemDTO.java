package cn.aitenry.iims.integral.model.dto.wiki;

import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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
public class UpdateWikiCatalogItemDTO {

    /**
     * 目录 ID
     */
    @NotNull(message = "目录 ID 不能为空")
    private Long id;

    private Long docId;

    private DocumentTypeEnum type;

    @NotBlank(message = "目录标题不能为空")
    private String title;

    private Integer sort;

    private Integer level;

    private Boolean isEmbedding;

    /**
     * 子目录
     */
    @Valid
    private List<UpdateWikiCatalogItemDTO> children;

}
