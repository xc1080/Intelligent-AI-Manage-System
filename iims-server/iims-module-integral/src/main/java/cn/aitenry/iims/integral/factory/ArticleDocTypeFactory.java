package cn.aitenry.iims.integral.factory;

import cn.aitenry.iims.integral.model.vo.article.FindArticleDetailVO;
import cn.aitenry.iims.integral.service.ArticleService;
import cn.aitenry.iims.ai.rag.factory.DocTypeService;
import cn.aitenry.iims.ai.rag.utils.MarkdownSplitter;
import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import cn.aitenry.iims.common.model.entity.integral.Article;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Service
public class ArticleDocTypeFactory implements DocTypeService {

    private final ArticleService articleService;

    public ArticleDocTypeFactory(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public DocumentTypeEnum getType() {
        return DocumentTypeEnum.ARTICLE;
    }

    @Override
    public List<Document> getDocument(Long id) {
        FindArticleDetailVO articleDetail = articleService.findArticleDetail(id);
        return MarkdownSplitter.splitByHeadersWithHierarchy(articleDetail.getContent(), new HashMap<>());
    }

    @Override
    public String getName(Long id) {
        Article article = articleService.selectById(id);
        return article.getTitle();
    }
}
