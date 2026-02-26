package cn.aitenry.iims.integral.service.impl;

import com.github.pagehelper.Page;
import cn.aitenry.iims.ai.chat.model.dto.CreateModelDTO;
import cn.aitenry.iims.ai.chat.model.dto.ModelPageQueryDTO;
import cn.aitenry.iims.ai.chat.model.dto.UpdateModelDTO;
import cn.aitenry.iims.ai.chat.model.entity.AiModel;
import cn.aitenry.iims.ai.chat.model.vo.ModelVO;
import cn.aitenry.iims.ai.chat.service.AiChatModelsService;
import cn.aitenry.iims.ai.chat.service.AiChatSettingService;
import cn.aitenry.iims.common.model.vo.BaseUserInfoVO;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.integral.service.UserService;
import cn.aitenry.iims.integral.service.AiModelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 14:32
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class AiModelServiceImpl implements AiModelService {

    public final AiChatModelsService aiChatModelsService;

    public final AiChatSettingService aiChatSettingService;

    public final UserService userService;

    public AiModelServiceImpl(AiChatModelsService aiChatModelsService, AiChatSettingService aiChatSettingService, UserService userService) {
        this.aiChatModelsService = aiChatModelsService;
        this.aiChatSettingService = aiChatSettingService;
        this.userService = userService;
    }

    @Override
    public PageResult pageQuery(ModelPageQueryDTO modelPageQueryDTO) {
        try (Page<ModelVO> modelVOS = aiChatModelsService.pageQuery(modelPageQueryDTO)) {
            long total = modelVOS.getTotal();
            List<ModelVO> results = modelVOS.getResult();
            results.forEach(modelVO -> {
                Long modelId = modelVO.getId();
                List<Long> userIds = aiChatSettingService.selectUserIdByModelId(modelId);
                List<BaseUserInfoVO> adminBases = userService.getUserBaseInfoByIds(userIds);
                modelVO.setUsers(adminBases);
            });
            return new PageResult(total, results);
        }
    }

    @Override
    public boolean deleteModel(Long id) {
        return aiChatModelsService.deleteModelById(id);
    }

    @Override
    public boolean updateModel(UpdateModelDTO model) {
        AiModel build = AiModel.builder().id(model.getId()).rename(model.getRename()).description(model.getDescription())
                .token(model.getToken()).modelType(model.getModelType()).type(model.getType())
                .name(model.getName()).url(model.getUrl()).key(model.getKey())
                .build();
        return aiChatModelsService.updateModel(build);
    }

    @Override
    public boolean createModel(CreateModelDTO model) {
        AiModel build = AiModel.builder().rename(model.getRename()).description(model.getDescription())
                .token(model.getToken()).modelType(model.getModelType()).type(model.getType())
                .name(model.getName()).url(model.getUrl()).key(model.getKey())
                .build();
        return aiChatModelsService.insertModel(build);
    }

}
