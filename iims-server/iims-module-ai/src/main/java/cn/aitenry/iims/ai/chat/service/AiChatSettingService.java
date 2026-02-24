package cn.aitenry.iims.ai.chat.service;

import cn.aitenry.iims.ai.chat.model.entity.ModelSetting;

import java.util.List;

public interface AiChatSettingService {

    ModelSetting getUserModelSetting();

    List<Long> selectUserIdByModelId(Long modelId);

}
