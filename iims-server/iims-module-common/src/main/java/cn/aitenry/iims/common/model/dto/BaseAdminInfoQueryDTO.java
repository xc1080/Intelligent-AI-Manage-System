package cn.aitenry.iims.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
public class BaseAdminInfoQueryDTO {
    @ApiModelProperty("用户名")
    private String username;
}
