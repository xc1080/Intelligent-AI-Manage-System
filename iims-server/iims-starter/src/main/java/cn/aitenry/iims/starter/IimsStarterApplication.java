package cn.aitenry.iims.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@SpringBootApplication(exclude = {
        org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreAutoConfiguration.class
})
@EnableTransactionManagement //开启注解方式的事务管理
@EnableCaching
@ComponentScan({"cn.aitenry.iims.*"}) // 多模块项目中，必需手动指定扫描包下面的所有类
public class IimsStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(IimsStarterApplication.class, args);
    }

}
