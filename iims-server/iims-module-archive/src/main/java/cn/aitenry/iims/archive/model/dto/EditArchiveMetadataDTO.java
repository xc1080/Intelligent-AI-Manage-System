package cn.aitenry.iims.archive.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EditArchiveMetadataDTO extends ArchiveMetadataDTO {

    @ApiModelProperty("档案ID")
    private Long id;

}
