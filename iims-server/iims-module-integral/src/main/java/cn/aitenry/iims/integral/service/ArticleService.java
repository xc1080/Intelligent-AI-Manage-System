package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.common.model.entity.integral.Article;
import cn.aitenry.iims.common.model.entity.integral.ArticleContent;
import cn.aitenry.iims.common.model.entity.integral.DictValue;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.integral.model.dto.article.FindArticleDetailDTO;
import cn.aitenry.iims.integral.model.dto.article.FindArticlePageListDTO;
import cn.aitenry.iims.integral.model.dto.article.PublishArticleDTO;
import cn.aitenry.iims.integral.model.dto.article.UpdateArticleDTO;
import cn.aitenry.iims.integral.model.vo.article.FindArticleDetailVO;
import cn.aitenry.iims.integral.model.vo.article.FindArticleInfoDetailVO;

import java.util.List;

public interface ArticleService {
    /**
     * 发布文章
     */
    void publishArticle(PublishArticleDTO publishArticleDto);

    /**
     * 删除文章
     */
    void deleteArticle(List<Long> ids);

    void updateChunkKeys(Long id, List<String> chunkKeys);

    /**
     * 查询文章分页数据
     */
    PageResult findArticlePageList(FindArticlePageListDTO findArticlePageListDto);

    /**
     * 查询文章详情
     */
    FindArticleDetailVO findArticleDetail(Long articleId);

    Article selectById(Long articleId);

    ArticleContent selectByArticleId(Long articleId);

    List<DictValue> selectTagsByArticleId(Long articleId);

    DictValue selectCategoryByArticleId(Long articleId);

    /**
     * 更新文章
     */
    void updateArticle(UpdateArticleDTO updateArticleDto);

    /**
     * 更新文章是否置顶
     */
    void updateArticleIsTop(Boolean isTop, Long id);

    void updateTypeByIds(Integer type, List<Long> articleIds);

    Article selectNextArticle(Long articleId);

    Article selectPreArticle(Long articleId);

    void buildArticleByFileId(Long fileId);

    FindArticleInfoDetailVO findArticleInfoDetail(FindArticleDetailDTO dto);
}
