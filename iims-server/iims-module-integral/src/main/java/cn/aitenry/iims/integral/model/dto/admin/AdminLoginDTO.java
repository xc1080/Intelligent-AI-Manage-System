package cn.aitenry.iims.integral.model.dto.admin;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "管理员登录时传递的数据模型")
public class AdminLoginDTO implements Serializable {

    //用户名
    @ApiModelProperty("用户名")
    private String username;

    //用户邮箱
    @ApiModelProperty("用户邮箱")
    private String email;

    //身份证号码
    @ApiModelProperty("身份证号码")
    private String idNumber;

    //手机号
    @ApiModelProperty("手机号")
    private String phone;

    //密码
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("密码")
    private String uuid;

}