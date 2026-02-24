package cn.aitenry.iims.ai.rag.factory.impl;

import cn.aitenry.iims.ai.rag.enums.DomProcessEnum;
import cn.aitenry.iims.ai.rag.factory.DocumentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class DocumentServiceFactory {
    private final Map<DomProcessEnum, DocumentService> services;

    public DocumentServiceFactory(List<DocumentService> serviceList) {
        this.services = serviceList.stream()
                .collect(Collectors.toMap(
                        DocumentService::getType,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

    public DocumentService getService(DomProcessEnum type) {
        return Optional.ofNullable(services.get(type))
                .orElseThrow(() -> new IllegalArgumentException("No service found for type: " + type));
    }
}