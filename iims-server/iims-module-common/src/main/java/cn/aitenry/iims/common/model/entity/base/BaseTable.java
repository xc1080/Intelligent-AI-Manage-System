package cn.aitenry.iims.common.model.entity.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
public class BaseTable {

    //创建人ID
    @ApiModelProperty("创建人ID")
    private Long createBy;

    //创建时间
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    //最后修改人ID
    @ApiModelProperty("最后修改人ID")
    private Long updateBy;

    //最后修改时间
    @ApiModelProperty("最后修改时间")
    private LocalDateTime updateTime;

}
