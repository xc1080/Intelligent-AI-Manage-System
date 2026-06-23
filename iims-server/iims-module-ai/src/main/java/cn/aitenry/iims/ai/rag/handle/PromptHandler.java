package cn.aitenry.iims.ai.rag.handle;

import cn.aitenry.iims.ai.chat.model.entity.ModelUseInfo;
import cn.aitenry.iims.common.model.entity.file.FileWarehouse;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public interface PromptHandler {
    ModelUseInfo handle(FileWarehouse file);
}