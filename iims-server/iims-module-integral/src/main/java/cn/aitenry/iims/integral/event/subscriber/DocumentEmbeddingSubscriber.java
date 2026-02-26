package cn.aitenry.iims.integral.event.subscriber;

import cn.aitenry.iims.ai.rag.service.MilvusStoreService;
import cn.aitenry.iims.common.utils.SnowFlakeIdWorker;
import cn.aitenry.iims.integral.event.DocumentEmbeddingEvent;
import cn.aitenry.iims.subscriber.enums.NotificationLevelEnum;
import cn.aitenry.iims.subscriber.model.entity.NotificationData;
import cn.aitenry.iims.subscriber.model.entity.SseMessage;
import cn.aitenry.iims.subscriber.service.SseNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

    private final SnowFlakeIdWorker snowFlakeIdWorker;

    private final SseNotificationService notificationService;

    public DocumentEmbeddingSubscriber(MilvusStoreService milvusStoreService, SnowFlakeIdWorker snowFlakeIdWorker, SseNotificationService notificationService) {
        this.milvusStoreService = milvusStoreService;
        this.snowFlakeIdWorker = snowFlakeIdWorker;
        this.notificationService = notificationService;
    }

    public long uniqueId() {
        return snowFlakeIdWorker.nextId();
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

        // 文档向量化
        Boolean aBoolean = milvusStoreService.addDocumentByWiki(wikiId);
        SseMessage<NotificationData> message = SseMessage.notification(
                uniqueId(), new NotificationData("知识库向量化", "已完成知识库向量化",
                        NotificationLevelEnum.SUCCESS, LocalDateTime.now())
        );
        notificationService.sendToClient(currentId, message);
        log.info("==> 知识库向量化事件消费成功，wikiId: {}，知识库向量化结果: {}", wikiId, aBoolean);
    }
}