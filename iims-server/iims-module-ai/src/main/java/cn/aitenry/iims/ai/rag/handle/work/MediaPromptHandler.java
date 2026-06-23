package cn.aitenry.iims.ai.rag.handle.work;

import cn.aitenry.iims.ai.rag.aspect.FileTypeHandler;
import cn.aitenry.iims.ai.rag.enums.FileModelTypeEnum;
import cn.aitenry.iims.ai.rag.handle.PromptHandler;
import cn.aitenry.iims.ai.chat.model.entity.ModelUseInfo;
import cn.aitenry.iims.common.model.entity.file.FileWarehouse;
import org.springframework.stereotype.Component;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Component
@FileTypeHandler({"jpg", "png"})
public class MediaPromptHandler implements PromptHandler {

    @Override
    public ModelUseInfo handle(FileWarehouse file) {
        // 根据具体文件类型执行逻辑
        return ModelUseInfo.builder().type(FileModelTypeEnum.IMAGE).build();
    }
}