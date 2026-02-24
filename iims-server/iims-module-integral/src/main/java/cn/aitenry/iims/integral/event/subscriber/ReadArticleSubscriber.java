package cn.aitenry.iims.integral.event.subscriber;

import cn.aitenry.iims.integral.event.ReadArticleEvent;
import cn.aitenry.iims.integral.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReadArticleSubscriber implements ApplicationListener<ReadArticleEvent> {

    private final ArticleMapper articleMapper;

    public ReadArticleSubscriber(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(ReadArticleEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long articleId = event.getArticleId();

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();

        log.info("==> 文章阅读事件: {}", threadName);

        // 执行文章阅读量 +1
        articleMapper.increaseReadNum(articleId);
        log.info("==> 文章阅读事件消费成功，文章阅读量 +1 操作成功，articleId: {}", articleId);
    }
}