package cn.aitenry.iims.integral.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmbeddingCount {

    private Long total;

    private Long count;

}
