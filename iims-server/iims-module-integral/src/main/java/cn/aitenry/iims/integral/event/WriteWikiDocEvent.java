package cn.aitenry.iims.integral.event;

import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Getter
public class WriteWikiDocEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private final Long docId;

    private final Long currentId;

    private final DocumentTypeEnum type;

    public WriteWikiDocEvent(Object source, Long docId, Long currentId, DocumentTypeEnum type) {
        super(source);
        this.docId = docId;
        this.currentId = currentId;
        this.type = type;
    }

}
