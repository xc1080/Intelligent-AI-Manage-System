package cn.aitenry.iims.ai.rag.factory.realize;

import cn.aitenry.iims.ai.rag.factory.DocTypeService;
import cn.aitenry.iims.common.enums.DocumentTypeEnum;
import cn.aitenry.iims.common.service.MinioService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileDocTypeFactory implements DocTypeService {

    private final MinioService minioService;

    public FileDocTypeFactory(MinioService minioService) {
        this.minioService = minioService;
    }

    @Override
    public DocumentTypeEnum getType() {
        return DocumentTypeEnum.FILE;
    }

    @Override
    public List<Document> getDocument(Long id) {
        Resource resource = new InputStreamResource(minioService.getInputStream(id));
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(
                3000, 1000, 7,
                30, true);
        return tokenTextSplitter.split(tikaDocumentReader.read());
    }

    @Override
    public String getName(Long id) {
        return null;
    }
}
