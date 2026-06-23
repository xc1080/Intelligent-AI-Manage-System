package cn.aitenry.iims.integral.model.dto.organization;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author: Aitenry
 * @Date: 2026/3/2 21:52
 * @Version: v1.0.0

 **/
@Data
@ApiModel(description = "组织的数据模型")
public class OrganizationDTO {
    private Long id;
    private String code;
    private String description;
    private String name;
    private Long parentId;
    private Integer type;

}
