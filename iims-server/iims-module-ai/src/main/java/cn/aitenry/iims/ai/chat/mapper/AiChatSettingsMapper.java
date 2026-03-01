package cn.aitenry.iims.ai.chat.mapper;

import cn.aitenry.iims.ai.chat.model.entity.ModelSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiChatSettingsMapper {

    ModelSetting getModelSettingByUserId(Long userId);

    List<Long> selectUserIdByModelId(@Param("modelId") Long modelId);

}
