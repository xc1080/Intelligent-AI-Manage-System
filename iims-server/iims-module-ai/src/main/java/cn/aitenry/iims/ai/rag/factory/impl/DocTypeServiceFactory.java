package cn.aitenry.iims.ai.rag.factory.impl;

import cn.aitenry.iims.ai.rag.factory.DocTypeService;
import cn.aitenry.iims.common.enums.DocumentTypeEnum;
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
public class DocTypeServiceFactory {

    private final Map<DocumentTypeEnum, DocTypeService> services;

    public DocTypeServiceFactory(List<DocTypeService> serviceList) {
        this.services = serviceList.stream()
                .collect(Collectors.toMap(
                        DocTypeService::getType,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

    public DocTypeService getService(DocumentTypeEnum type) {
        return Optional.ofNullable(services.get(type))
                .orElseThrow(() -> new IllegalArgumentException("No service found for type: " + type));
    }

}
