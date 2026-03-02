package cn.aitenry.iims.integral.event.subscriber;

import cn.aitenry.iims.common.context.BaseContext;
import cn.aitenry.iims.integral.event.WriteWikiDocEvent;
import cn.aitenry.iims.integral.mapper.WikiCatalogMapper;
import cn.aitenry.iims.ai.rag.service.MilvusStoreService;
import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import cn.aitenry.iims.common.model.entity.integral.WikiCatalog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Component
@Slf4j
public class WriteWikiDocSubscriber implements ApplicationListener<WriteWikiDocEvent> {

    private final WikiCatalogMapper wikiCatalogMapper;
    private final MilvusStoreService milvusStoreService;

    public WriteWikiDocSubscriber(WikiCatalogMapper wikiCatalogMapper, MilvusStoreService milvusStoreService) {
        this.wikiCatalogMapper = wikiCatalogMapper;
        this.milvusStoreService = milvusStoreService;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(WriteWikiDocEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long docId = event.getDocId();
        DocumentTypeEnum type = event.getType();
        Long currentId = event.getCurrentId();

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();
        log.info("==> 知识库文章更新向量库: {}", threadName);
        BaseContext.setCurrentId(currentId);
        WikiCatalog wikiCatalog = wikiCatalogMapper.selectWikiByDoc(docId, type);
        Boolean isEmbedding = wikiCatalog.getIsEmbedding();
        if (isEmbedding) {
            Long wikiId = wikiCatalog.getWikiId();
            wikiCatalogMapper.updateIsEmbedding(List.of(wikiCatalog.getId()), false);
            milvusStoreService.addDocumentByWiki(wikiId);
            log.info("==> 知识库文章更新向量库内容事件消费成功，wikiId: {}，docId: {}，type: {}", wikiId, docId, type);
        }
    }

}
