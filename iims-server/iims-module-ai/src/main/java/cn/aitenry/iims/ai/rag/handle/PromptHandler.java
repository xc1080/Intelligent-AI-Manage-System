package cn.aitenry.iims.ai.rag.handle;

import cn.aitenry.iims.ai.chat.model.entity.ModelUseInfo;
import cn.aitenry.iims.common.model.entity.file.FileWarehouse;

public interface PromptHandler {
    ModelUseInfo handle(FileWarehouse file);
}