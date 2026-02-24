package cn.aitenry.iims.auth.config;

import cn.aitenry.iims.common.utils.SnowFlakeIdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Configuration
public class SnowFlakeConfig {

    @Bean
    public SnowFlakeIdWorker adminSnowFlakeIdWorker() {
        return new SnowFlakeIdWorker(1, 1);
    }

    @Bean
    public SnowFlakeIdWorker archiveSnowFlakeIdWorker() {
        return new SnowFlakeIdWorker(2, 2);
    }

    @Bean
    public SnowFlakeIdWorker aiSnowFlakeIdWorker() {
        return new SnowFlakeIdWorker(3, 3);
    }

    @Bean
    public SnowFlakeIdWorker commonSnowFlakeIdWorker() {
        return new SnowFlakeIdWorker(6, 6);
    }
}