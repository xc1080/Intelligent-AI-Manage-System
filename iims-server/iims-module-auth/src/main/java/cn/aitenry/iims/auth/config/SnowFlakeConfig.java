package cn.aitenry.iims.auth.config;

import cn.aitenry.iims.common.utils.SnowFlakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 雪花ID生成器配置
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 统一使用单个全局ID生成器，workerId通过环境变量或IP自动分配
 *
 **/
@Configuration
@Slf4j
public class SnowFlakeConfig {

    @Bean
    public SnowFlakeIdWorker snowFlakeIdWorker() {
        long workerId = getWorkerId();
        long datacenterId = getDatacenterId();

        // 校验范围 (0-31)
        if (workerId < 0 || workerId > 31) {
            throw new IllegalArgumentException("workerId 必须在 0-31 范围内");
        }
        if (datacenterId < 0 || datacenterId > 31) {
            throw new IllegalArgumentException("datacenterId 必须在 0-31 范围内");
        }

        log.info("初始化雪花ID生成器: workerId={}, datacenterId={}", workerId, datacenterId);
        return new SnowFlakeIdWorker(workerId, datacenterId);
    }

    private long getWorkerId() {
        String envValue = System.getenv("SNOWFLAKE_WORKER_ID");
        if (envValue != null && !envValue.isEmpty()) {
            try {
                return Long.parseLong(envValue) % 32;
            } catch (NumberFormatException e) {
                log.warn("环境变量 SNOWFLAKE_WORKER_ID 格式错误，使用默认值: {}", envValue);
            }
        }
        return generateFromIp() % 32; // 本地开发环境自动计算
    }

    private long getDatacenterId() {
        String envValue = System.getenv("SNOWFLAKE_DATACENTER_ID");
        if (envValue != null && !envValue.isEmpty()) {
            try {
                return Long.parseLong(envValue) % 32;
            } catch (NumberFormatException e) {
                log.warn("环境变量 SNOWFLAKE_DATACENTER_ID 格式错误，使用默认值: {}", envValue);
            }
        }
        return 1; // 默认数据中心ID
    }

    private long generateFromIp() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return Math.abs(ip.hashCode()) % 32;
        } catch (UnknownHostException e) {
            log.warn("无法获取本地IP，使用默认workerId=1", e);
            return 1;
        }
    }
}