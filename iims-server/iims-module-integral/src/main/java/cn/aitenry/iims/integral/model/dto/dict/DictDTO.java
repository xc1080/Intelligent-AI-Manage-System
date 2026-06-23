package cn.aitenry.iims.integral.model.dto.dict;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DictDTO {

    private Long id;

    private String name;

    private Boolean isCanChange;

    private String remark;

}
