package cn.aitenry.iims.archive.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class AddArchiveMetadataDTO extends ArchiveMetadataDTO {

    @ApiModelProperty("档案归属ID[档案菜单ID]")
    private Integer metadataOwnership;

}
