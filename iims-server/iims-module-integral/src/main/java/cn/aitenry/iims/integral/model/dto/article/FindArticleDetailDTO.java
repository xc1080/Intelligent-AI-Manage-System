package cn.aitenry.iims.integral.model.dto.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询文章详情 VO")
public class FindArticleDetailDTO {
    /**
     * 文章 ID
     */
    private Long articleId;
}