package cn.aitenry.iims.common.utils;

import cn.aitenry.iims.common.mapper.LogsMapper;
import cn.aitenry.iims.common.model.entity.log.Logs;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Component
public class LogDatabaseUtil implements ApplicationContextAware, ApplicationListener<ContextClosedEvent> {

    private static LogsMapper logsMapper;
    private static SnowFlakeIdWorker snowFlakeIdWorker;
    private static volatile boolean isApplicationClosing = false;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        try {
            logsMapper = context.getBean(LogsMapper.class);
            snowFlakeIdWorker = context.getBean("snowFlakeIdWorker", SnowFlakeIdWorker.class);
        } catch (Exception e) {
            System.err.println("Failed to initialize LogDatabaseUtil: " + e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        isApplicationClosing = true;
    }

    public static void saveLog(String level, String message, String loggerName, String threadName) {
        if (isApplicationClosing) {
            return;
        }

        if (logsMapper == null || snowFlakeIdWorker == null) {
            return;
        }

        Logs log = Logs.builder()
                .id(snowFlakeIdWorker.nextId())
                .level(level)
                .message(message)
                .loggerName(loggerName)
                .threadName(threadName)
                .createTime(LocalDateTime.now())
                .build();
        logsMapper.insert(log);
    }
}