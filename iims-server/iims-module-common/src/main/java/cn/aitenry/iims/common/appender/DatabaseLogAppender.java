package cn.aitenry.iims.common.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import cn.aitenry.iims.common.utils.LogDatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 数据库日志记录存储
 **/
@Slf4j
@Component
public class DatabaseLogAppender extends AppenderBase<ILoggingEvent> implements ApplicationListener<ContextClosedEvent> {

    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    // 可以根据需要调整最低记录级别，避免过多日志
    private final Level minimumLogLevel = Level.WARN;

    public DatabaseLogAppender() {
        super();
    }

    @Override
    public void start() {
        super.start();
        initialized.set(true);
    }

    @Override
    public void stop() {
        isShuttingDown.set(true);
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        // 检查是否正在关闭或未初始化
        if (isShuttingDown.get() || !initialized.get()) {
            return;
        }

        // 检查日志级别，只记录指定级别及以上的日志
        if (event.getLevel().toInt() <= minimumLogLevel.toInt()) {
            return;
        }

        try {
            // 使用异步方式写入数据库，避免阻塞日志记录
            CompletableFuture.runAsync(() -> {
                // 再次检查是否正在关闭
                if (isShuttingDown.get()) {
                    return;
                }

                try {
                    LogDatabaseUtil.saveLog(
                            String.valueOf(event.getLevel()),
                            event.getFormattedMessage(),
                            event.getLoggerName(),
                            event.getThreadName()
                    );
                } catch (Exception e) {
                    // 发生异常时不要重新记录到日志，避免循环
                    log.error("Failed to save log to database: {}", e.getMessage());
                    // 可以选择记录到本地文件或其他存储
                }
            });
        } catch (Exception e) {
            // 如果异步执行失败，记录到控制台但不影响主流程
            log.error("Error submitting async log task: {}", e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        isShuttingDown.set(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}