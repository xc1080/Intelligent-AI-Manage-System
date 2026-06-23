package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.ArticleContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Mapper
public interface ArticleContentMapper {

    @AutoFill(value = OperationType.INSERT)
    int insert(ArticleContent articleContent);

    @AutoFill(value = OperationType.UPDATE)
    void updateByArticleId(ArticleContent articleContent);

    ArticleContent selectByArticleId(Long articleId);
}
