package cn.aitenry.iims.common.model.entity.log;

import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor /*注解在类上；为类提供一个无参的构造方法*/
@AllArgsConstructor /*注解在类上；为类提供一个全参的构造方法*/
@Builder
public class Logs extends BaseTable {

    private Long id;

    private String level;

    private String threadName;

    private String message;

    private String loggerName;

    private LocalDateTime createTime;

}
