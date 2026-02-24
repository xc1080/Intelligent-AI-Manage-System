package cn.aitenry.iims.archive.model.vo;

import cn.aitenry.iims.archive.model.entity.ArchiveBaseMetadata;
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
public class ArchiveMetadataVO extends ArchiveBaseMetadata {

    @ApiModelProperty("档案摘要")
    private String archivalAbstract;

    @ApiModelProperty("不公共属性")
    private String metadataProperty;

}
