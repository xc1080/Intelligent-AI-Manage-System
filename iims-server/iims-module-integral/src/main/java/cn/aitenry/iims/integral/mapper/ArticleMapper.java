package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Article;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import cn.aitenry.iims.common.model.entity.status.TypeStatus;
import cn.aitenry.iims.integral.model.vo.article.FindArticlePageListVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface ArticleMapper {

    @AutoFill(value = OperationType.INSERT)
    int insert(Article article);

    @AutoFill(value = OperationType.UPDATE)
    int updateById(Article article);

    @AutoFill(value = OperationType.UPDATE)
    void updateTypeByIds(TypeStatus typeStatus);

    Page<FindArticlePageListVO> pageQuery(String title, LocalDateTime startDate, LocalDateTime endDate, Integer type);

    int selectMaxWeight();

    Article selectById(Long articleId);

    void updateArticleDeleted(DeletedStatus deletedStatus);

    Article selectNextArticle(Long articleId);

    Article selectPreArticle(Long articleId);

    void increaseReadNum(Long articleId);
}
