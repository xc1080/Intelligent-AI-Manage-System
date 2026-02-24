package cn.aitenry.iims.ai.chat.service.impl;

import cn.aitenry.iims.ai.chat.mapper.AiChatSettingsMapper;
import cn.aitenry.iims.ai.chat.model.entity.ModelSetting;
import cn.aitenry.iims.ai.chat.service.AiChatSettingService;
import cn.aitenry.iims.common.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AiChatSettingServiceImpl implements AiChatSettingService {

    private final AiChatSettingsMapper settingsMapper;

    public AiChatSettingServiceImpl(AiChatSettingsMapper settingsMapper) {
        this.settingsMapper = settingsMapper;
    }

    @Override
    public ModelSetting getUserModelSetting() {
        return settingsMapper.getModelSettingByUserId(BaseContext.getCurrentId());
    }

    @Override
    public List<Long> selectUserIdByModelId(Long modelId) {
        return settingsMapper.selectUserIdByModelId(modelId);
    }
}
