package cn.aitenry.iims.integral.model.dto.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
public class RolePageQueryDTO implements Serializable {

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("权限字符")
    private String roleEn;

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页显示记录数")
    private int pageSize;
}
