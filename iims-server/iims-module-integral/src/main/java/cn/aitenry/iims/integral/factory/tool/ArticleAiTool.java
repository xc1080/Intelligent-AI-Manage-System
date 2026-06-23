package cn.aitenry.iims.integral.factory.tool;

import com.alibaba.fastjson.JSONObject;
import cn.aitenry.iims.ai.tools.factory.AITool;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.integral.model.dto.article.FindArticlePageListDTO;
import cn.aitenry.iims.integral.service.ArticleService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * @Author: Aitenry
 * @Date: 2025/12/27 10:24
 * @Version: v1.0.0

 **/
@Component
public class ArticleAiTool implements AITool {

    private final ArticleService articleService;

    public ArticleAiTool(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public Object getToolInstance() {
        return this;
    }

    @Override
    public String getToolName() {
        return "article-tools";
    }

    @Tool(description = "分页查询文档列表内容")
    public String findArticlePageList(
            @ToolParam(description = "文章的标题", required = false) String title,
            @ToolParam(description = "页码") Integer page,
            @ToolParam(description = "每页显示数") Integer pageSize) {
        try {
            PageResult articlePageList = articleService.findArticlePageList(FindArticlePageListDTO.builder()
                    .title(title).page(page).pageSize(pageSize).build());
            return JSONObject.toJSONString(articlePageList);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
