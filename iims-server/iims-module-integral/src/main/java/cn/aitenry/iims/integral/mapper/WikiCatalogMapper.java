package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.WikiCatalog;
import cn.aitenry.iims.integral.model.entity.DeletedWikiStatus;
import cn.aitenry.iims.integral.model.entity.EmbeddingCount;
import cn.aitenry.iims.integral.model.vo.wiki.WikiCatalogVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface WikiCatalogMapper {

    /**
     * 新增知识库目录
     *
     * @param wikiCatalog WikiCatalog
     */
    @AutoFill(value = OperationType.INSERT)
    int insert(WikiCatalog wikiCatalog);

    List<WikiCatalog> selectByWikiId(Long wikiId);

    EmbeddingCount countEmbedding(Long wikiId);

    @AutoFill(value = OperationType.UPDATE)
    void deleteByWikiId(DeletedWikiStatus deletedWikiStatus);

    WikiCatalog selectFirstArticleId(Long wikiId);

    @AutoFill(value = OperationType.INSERT)
    void insertBatchSomeColumn(List<WikiCatalog> wikiCatalogList);

    WikiCatalogVO selectByWikiIdAndArticleId(Long wikiId, Long docId);

    WikiCatalogVO selectPreArticle(Long wikiId, Long catalogId);

    WikiCatalogVO selectNextArticle(Long wikiId, Long catalogId);

    void updateIsEmbedding(List<Long> ids, Boolean isEmbedding);

    WikiCatalog selectWikiByDoc(Long docId, DocumentTypeEnum type);

}
