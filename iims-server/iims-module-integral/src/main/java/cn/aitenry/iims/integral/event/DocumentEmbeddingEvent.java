package cn.aitenry.iims.integral.event;

import cn.aitenry.iims.ai.rag.enums.DomProcessEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Getter
public class DocumentEmbeddingEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private final Long wikiId;

    private final Long currentId;

    private final DomProcessEnum domProcess;

    public DocumentEmbeddingEvent(Object source, Long wikiId, Long currentId, DomProcessEnum domProcess) {
        super(source);
        this.wikiId = wikiId;
        this.currentId = currentId;
        this.domProcess = domProcess;
    }
}