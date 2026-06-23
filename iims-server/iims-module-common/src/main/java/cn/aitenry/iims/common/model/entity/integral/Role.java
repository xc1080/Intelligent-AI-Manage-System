package cn.aitenry.iims.common.model.entity.integral;

import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;


/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@Builder /*@Builder可以让你类链式的调用你的代码，来初始化你的实例对象*/
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseTable implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String roleName;

    private String roleEn;

    private String menus;

    private String info;

    private Integer systemic;

}