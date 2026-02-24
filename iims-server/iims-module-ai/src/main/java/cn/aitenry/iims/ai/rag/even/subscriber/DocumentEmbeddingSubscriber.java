package cn.aitenry.iims.ai.rag.even.subscriber;

import cn.aitenry.iims.ai.rag.even.DocumentEmbeddingEvent;
import cn.aitenry.iims.ai.rag.service.MilvusStoreService;
import cn.aitenry.iims.subscriber.model.entity.SseMessage;
import cn.aitenry.iims.subscriber.service.SseNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Component
@Slf4j
public class DocumentEmbeddingSubscriber implements ApplicationListener<DocumentEmbeddingEvent> {

    private final MilvusStoreService milvusStoreService;

    private final SseNotificationService notificationService;

    public DocumentEmbeddingSubscriber(MilvusStoreService milvusStoreService, SseNotificationService notificationService) {
        this.milvusStoreService = milvusStoreService;
        this.notificationService = notificationService;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(DocumentEmbeddingEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long wikiId = event.getWikiId();
        Long currentId = event.getCurrentId();
        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();

        log.info("==> 知识库向量化事件: {}", threadName);

        // 执行文章阅读量 +1
        Boolean aBoolean = milvusStoreService.addDocumentByWiki(wikiId);
        notificationService.sendToClient(
                currentId, new SseMessage("DocumentEmbeddingEvent", wikiId));
        log.info("==> 知识库向量化事件消费成功，wikiId: {}，知识库向量化结果: {}", wikiId, aBoolean);
    }
}