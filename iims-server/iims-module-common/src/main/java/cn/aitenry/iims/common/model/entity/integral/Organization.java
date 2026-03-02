package cn.aitenry.iims.common.model.entity.integral;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.io.Serializable;

/**
 * @Author: Aitenry
 * @Date: 2025/11/15 22:59
 * @Version: v1.0.0
 * @Description: 组织表实体
 **/
@Data
@Builder /*@Builder可以让你类链式的调用你的代码，来初始化你的实例对象*/
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
@EqualsAndHashCode(callSuper = true)
public class Organization extends BaseTable implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;

    /**
     * 父级ID
     */
    private Long parentId;

    private Long jobId;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型：0 公司、1 部门、2 职位
     */
    private Integer type;

    /**
     * 描述
     */
    private String description;

    private String code;

    /**
     * 是否删除：0 存在、1 删除
     */
    private Integer isDeleted;

}
