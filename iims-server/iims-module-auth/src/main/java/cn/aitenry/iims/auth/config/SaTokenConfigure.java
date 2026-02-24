package cn.aitenry.iims.auth.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: Sa-Token 配置类
 **/
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**").excludePathPatterns("");
    }
}
