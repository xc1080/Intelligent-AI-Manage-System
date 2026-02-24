package cn.aitenry.iims.integral.mapper;

import com.github.pagehelper.Page;
import cn.aitenry.iims.integral.model.vo.article.FindArticlePageListVO;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Article;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import cn.aitenry.iims.common.model.entity.status.TypeStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface ArticleMapper {

    @AutoFill(value = OperationType.INSERT)
    int insert(Article article);

    @AutoFill(value = OperationType.UPDATE)
    int updateById(Article article);

    @AutoFill(value = OperationType.UPDATE)
    void updateTypeByIds(TypeStatus typeStatus);

    Page<FindArticlePageListVO> pageQuery(String title, LocalDateTime startDate, LocalDateTime endDate, Integer type);

    @Select("select weight from iims_integral_article order by weight desc limit 1")
    int selectMaxWeight();

    @Select("select * from iims_integral_article where id = #{articleId}")
    Article selectById(Long articleId);

    void updateArticleDeleted(DeletedStatus deletedStatus);

    @Select("""
            SELECT *
            FROM iims_integral_article
            WHERE id > #{articleId}
            ORDER BY id ASC
            LIMIT 1;""")
    Article selectNextArticle(Long articleId);

    @Select("""
            SELECT *
            FROM iims_integral_article
            WHERE id < #{articleId}
            ORDER BY id DESC
            LIMIT 1;""")
    Article selectPreArticle(Long articleId);

    @Update("""
            UPDATE iims_integral_article
            SET read_num = read_num + 1
            WHERE id = #{articleId};""")
    void increaseReadNum(Long articleId);
}
