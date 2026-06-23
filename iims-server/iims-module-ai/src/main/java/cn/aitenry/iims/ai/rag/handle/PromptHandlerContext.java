package cn.aitenry.iims.ai.rag.handle;

import cn.aitenry.iims.ai.rag.aspect.FileTypeHandler;
import cn.aitenry.iims.ai.rag.enums.FileModelTypeEnum;
import cn.aitenry.iims.ai.chat.model.entity.ModelUseInfo;
import cn.aitenry.iims.common.model.entity.file.FileWarehouse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Slf4j
@Service
public class PromptHandlerContext {

    private final Map<String, PromptHandler> handlerMap = new HashMap<>();

    private final ApplicationContext applicationContext;

    public PromptHandlerContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        // 扫描所有实现了 FileHandler 接口的 Bean
        Map<String, PromptHandler> beans = applicationContext.getBeansOfType(PromptHandler.class);
        for (PromptHandler handler : beans.values()) {
            FileTypeHandler annotation = handler.getClass().getAnnotation(FileTypeHandler.class);
            if (annotation != null) {
                for (String fileType : annotation.value()) {
                    handlerMap.put(fileType, handler);
                }
            }
        }
    }

    public ModelUseInfo handleFile(FileWarehouse file) {
        String fileType = file.getFileName();

        PromptHandler handler = handlerMap.get(getFileExtension(fileType));

        if (handler != null) {
            return handler.handle(file);
        }
        log.warn("No handler found for file type: {}", fileType);
        return ModelUseInfo.builder().type(FileModelTypeEnum.TEXT).build();
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1 || lastIndexOfDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1).toLowerCase();
    }
}