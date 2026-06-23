package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.ai.rag.enums.DomProcessEnum;
import cn.aitenry.iims.common.model.entity.integral.WikiCatalog;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.integral.model.dto.article.FindArticleDetailDTO;
import cn.aitenry.iims.integral.model.dto.wiki.*;
import cn.aitenry.iims.integral.model.vo.wiki.FindWikiArticleDetailVO;
import cn.aitenry.iims.integral.model.vo.wiki.FindWikiArticlePreNextVO;
import cn.aitenry.iims.integral.model.vo.wiki.FindWikiCatalogListVO;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public interface WikiService {
    /**
     * 新增知识库
     */
    void addWiki(AddWikiDTO addWikiDto);

    /**
     * 删除知识库
     */
    void deleteWiki(DeleteWikiDTO deleteWikiDto);

    /**
     * 知识库分页查询
     */
    PageResult findWikiPageList(FindWikiPageListDTO dto, Boolean openPublish);

    default PageResult findWikiPublishPageList(FindWikiPageListDTO dto) {
        return findWikiPageList(dto, true);
    }

    /**
     * 更新知识库置顶状态
     */
    void updateWikiIsTop(UpdateWikiIsTopDTO updateWikiIsTopDto);

    /**
     * 更新知识库发布状态
     */
    void updateWikiIsPublish(UpdateWikiIsPublishDTO updateWikiIsPublishDto);

    /**
     * 更新知识库
     */
    void updateWiki(UpdateWikiDTO updateWikiDto);

    /**
     * 查询知识库目录
     */
    List<FindWikiCatalogListVO> findWikiCatalogList(FindWikiCatalogListDTO findWikiCatalogListDto);

    /**
     * 更新知识库目录
     */
    void updateWikiCatalogs(UpdateWikiCatalogDTO updateWikiCatalogDto);

    List<WikiCatalog> findWikiById(Long wikiId);

    void updateIsEmbedding(List<Long> ids);

    FindWikiArticlePreNextVO findArticlePreNext(FindWikiArticlePreNextDTO articlePreNextDto);

    FindWikiArticleDetailVO findArticleDetail(FindArticleDetailDTO articleDetailDto);

    void documentEmbeddingByModel(Long wikiId, DomProcessEnum domProcessEnum);
}
