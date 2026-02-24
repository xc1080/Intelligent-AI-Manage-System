package cn.aitenry.iims.integral.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Getter
public class ReadArticleEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private final Long articleId;

    public ReadArticleEvent(Object source, Long articleId) {
        super(source);
        this.articleId = articleId;
    }
}