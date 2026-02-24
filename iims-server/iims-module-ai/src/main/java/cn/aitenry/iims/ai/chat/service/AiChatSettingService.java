package cn.aitenry.iims.ai.chat.service;

import cn.aitenry.iims.ai.chat.model.entity.ModelSetting;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface AiChatSettingService {

    ModelSetting getUserModelSetting();

    List<Long> selectUserIdByModelId(Long modelId);

}
