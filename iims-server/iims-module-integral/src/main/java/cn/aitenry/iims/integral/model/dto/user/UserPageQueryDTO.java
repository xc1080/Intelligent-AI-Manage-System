package cn.aitenry.iims.integral.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
public class UserPageQueryDTO implements Serializable {

    //用户真实姓名
    @ApiModelProperty("真实姓名")
    private String name;

    //用户用户名
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("账户状态（1 禁用、0 正常）")
    private Boolean isDisable;

    //页码
    @ApiModelProperty("页码")
    private int page;

    //每页显示记录数
    @ApiModelProperty("每页显示记录数")
    private int pageSize;

}
