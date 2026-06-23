package cn.aitenry.iims.integral.factory;

import cn.aitenry.iims.ai.rag.enums.DomProcessEnum;
import cn.aitenry.iims.ai.rag.factory.DocumentService;
import cn.aitenry.iims.ai.rag.service.CustomizeVectorStoreService;
import cn.aitenry.iims.ai.rag.utils.MarkdownSplitter;
import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import cn.aitenry.iims.common.model.entity.integral.WikiCatalog;
import cn.aitenry.iims.integral.model.vo.article.FindArticleDetailVO;
import cn.aitenry.iims.integral.service.ArticleService;
import cn.aitenry.iims.integral.service.WikiService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Service
public class WikiDocumentFactory implements DocumentService {

    private final ArticleService articleService;
    private final WikiService wikiService;
    private final CustomizeVectorStoreService storeService;

    private WikiDocumentFactory(ArticleService articleService, WikiService wikiService,
                                CustomizeVectorStoreService storeService) {
        this.articleService = articleService;
        this.wikiService = wikiService;
        this.storeService = storeService;
    }

    @Override
    public DomProcessEnum getType() {
        return DomProcessEnum.IIMS_INTEGRAL_ADD_DOM;
    }

    @Override
    public Boolean process(Long wikiId) {

        List<WikiCatalog> wikiCatalogs = wikiService.findWikiById(wikiId);
        List<Long> ids = new ArrayList<>();
        for (WikiCatalog wikiCatalog: wikiCatalogs) {
            Long docId = wikiCatalog.getDocId();
            if (wikiCatalog.getIsEmbedding() || Objects.isNull(docId)) {
                continue;
            }
            DocumentTypeEnum type = wikiCatalog.getType();
            String wikiIdStr = String.valueOf(wikiId);
            String docIdStr = String.valueOf(docId);

            FindArticleDetailVO articleDetail = articleService.findArticleDetail(docId);
            String content = articleDetail.getContent();
            List<Document> documents = MarkdownSplitter.splitByHeadersWithHierarchy(
                    content, Map.of("wikiId", wikiIdStr, "docId", docIdStr, "type", type));
            List<String> newChunkKeys = documents.stream().map(Document::getMetadata)
                    .map(item -> item.get("chunk_key").toString()).toList();
            articleService.updateChunkKeys(docId, newChunkKeys);
            List<String> oldChunkKeys = articleDetail.getChunkKeys();
            if (oldChunkKeys == null) {
                oldChunkKeys = new ArrayList<>();
            }
            Set<String> newChunkKeySet = new LinkedHashSet<>(newChunkKeys);
            Set<String> oldChunkKeySet = new LinkedHashSet<>(oldChunkKeys);

            List<String> addChunk = new ArrayList<>();
            List<String> delChunk = new ArrayList<>();

            for (String newKey : newChunkKeySet) {
                if (!oldChunkKeySet.contains(newKey)) addChunk.add(newKey);
            }
            for (String oldKey : oldChunkKeySet) {
                if (!newChunkKeySet.contains(oldKey)) delChunk.add(oldKey);
            }
            FilterExpressionBuilder filter = new FilterExpressionBuilder();
            if (!delChunk.isEmpty()) {
                for (String delKey : delChunk) {
                    storeService.loadMilvusVectorStore("iims", "wiki")
                            .delete(filter.and(filter.and(
                                    filter.eq("docId", String.valueOf(docId)),
                                    filter.eq("type", type.name())),
                                    filter.eq("chunk_key", delKey)).build());
                }
            }
            List<Document> documentsToAdd = documents.stream()
                    .filter(doc -> {
                        Object chunkKey = doc.getMetadata().get("chunk_key");
                        return chunkKey != null && addChunk.contains(chunkKey.toString());
                    }).toList();
            if (!documentsToAdd.isEmpty()) {
                storeService.loadMilvusVectorStore("iims", "wiki")
                        .add(documentsToAdd);
                ids.add(wikiCatalog.getId());
            }
        }
        if (!ids.isEmpty()) {
            wikiService.updateIsEmbedding(ids);
        }
        return true;
    }
}
