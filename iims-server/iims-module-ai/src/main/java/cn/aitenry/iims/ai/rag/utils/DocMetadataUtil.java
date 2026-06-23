package cn.aitenry.iims.ai.rag.utils;

import com.alibaba.fastjson.JSONArray;
import cn.aitenry.iims.ai.rag.factory.DocTypeService;
import cn.aitenry.iims.ai.rag.factory.impl.DocTypeServiceFactory;
import cn.aitenry.iims.ai.rag.model.entity.Metadata;
import cn.aitenry.iims.ai.rag.model.entity.DocMetadata;
import cn.aitenry.iims.ai.rag.model.entity.MetadataScore;
import cn.aitenry.iims.ai.chat.model.vo.DocMetadataVO;
import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Component
public class DocMetadataUtil {

    private final DocTypeServiceFactory docTypeServiceFactory;

    public DocMetadataUtil(DocTypeServiceFactory docTypeServiceFactory) {
        this.docTypeServiceFactory = docTypeServiceFactory;
    }

    public List<DocMetadataVO> getDocMetadata (String metadata) {

        if (Objects.nonNull(metadata)) {
            // 解析 metadata 为 DocMetadata 列表
            List<DocMetadata> docsMetadata = JSONArray.parseArray(metadata, DocMetadata.class);

            // 创建一个映射，用于存储 docId -> DocMetadataVo
            Map<Long, DocMetadataVO> docMetadataVoMap = new HashMap<>();

            // 遍历 docsMetadata，构建 DocMetadataVo 和 MetadataScore
            for (DocMetadata doc : docsMetadata) {
                Metadata docMetadata = doc.getMetadata();
                Long docId = docMetadata.getDocId();

                // 如果 docMetadataVoMap 中不存在该 docId，则初始化 DocMetadataVo
                docMetadataVoMap.computeIfAbsent(docId, k -> {
                    DocumentTypeEnum type = docMetadata.getType();
                    DocTypeService service = docTypeServiceFactory.getService(type);
                    docMetadata.setName(service.getName(docId));
                    return DocMetadataVO.builder()
                            .metadata(docMetadata)
                            .score(new ArrayList<>()) // 初始化为空列表
                            .build();
                });

                // 构造 MetadataScore 并添加到对应的 DocMetadataVo 的 score 列表中
                MetadataScore score = MetadataScore.builder()
                        .id(doc.getId())
                        .text(doc.getText())
                        .score(doc.getScore())
                        .index(doc.getMetadata().getChunkIndex())
                        .build();
                docMetadataVoMap.get(docId).getScore().add(score);
            }

            // 将 docMetadataVoMap 转换为列表并去重（利用 Set 去重）

            return docMetadataVoMap.values().stream()
                    .peek(vo -> vo.setScore(new ArrayList<>(new HashSet<>(vo.getScore())))) // 去重 score 列表
                    .collect(Collectors.toList());
        }
        return null;
    }

}
