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

 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long[] ids;

    private Integer type;

    private Long updateBy;

    private LocalDateTime updateTime;


}
