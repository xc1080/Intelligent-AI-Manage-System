package cn.aitenry.iims.common.model.entity.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@Builder /*@Builder可以让你类链式的调用你的代码，来初始化你的实例对象*/
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
public class DisableStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Boolean isDisable;

    private Long updateBy;

    private LocalDateTime updateTime;

}
