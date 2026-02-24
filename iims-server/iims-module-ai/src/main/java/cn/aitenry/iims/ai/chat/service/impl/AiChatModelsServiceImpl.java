package cn.aitenry.iims.ai.chat.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.aitenry.iims.ai.chat.mapper.AiChatModelsMapper;
import cn.aitenry.iims.ai.chat.model.dto.ModelPageQueryDTO;
import cn.aitenry.iims.ai.chat.model.entity.AiModel;
import cn.aitenry.iims.ai.chat.model.entity.ChatApi;
import cn.aitenry.iims.ai.chat.model.vo.ModelVO;
import cn.aitenry.iims.ai.chat.model.vo.SelectModelVO;
import cn.aitenry.iims.ai.chat.service.AiChatModelsService;
import cn.aitenry.iims.ai.chat.utils.AESEncryptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 20:32
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class AiChatModelsServiceImpl implements AiChatModelsService {

    public final AiChatModelsMapper aiChatModelsMapper;

    public AiChatModelsServiceImpl(AiChatModelsMapper aiChatModelsMapper) {
        this.aiChatModelsMapper = aiChatModelsMapper;
    }

    @Override
    public ChatApi selectModelById(Long modelId) {
        return aiChatModelsMapper.selectModelById(modelId);
    }

    @Override
    public Page<ModelVO> pageQuery(ModelPageQueryDTO modelPageQueryDTO) {
        int page = modelPageQueryDTO.getPage();
        int pageSize = modelPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);
        return aiChatModelsMapper.pageQuery(modelPageQueryDTO);
    }

    @Override
    public List<SelectModelVO> selectModelList() {
        return aiChatModelsMapper.selectModelList();
    }

    @Override
    public boolean deleteModelById(Long id) {
        return aiChatModelsMapper.deleteModelById(id);
    }

    @Override
    public boolean updateModel(AiModel aiModel) {
        String key = aiModel.getKey();
        if (StringUtils.isNoneBlank(key)) {
            aiModel.setKey(AESEncryptionUtil.encrypt(key));
        }
        return aiChatModelsMapper.update(aiModel);
    }

    @Override
    public boolean insertModel(AiModel aiModel) {
        String key = aiModel.getKey();
        if (StringUtils.isNoneBlank(key)) {
            aiModel.setKey(AESEncryptionUtil.encrypt(key));
        }
        aiModel.setIsOnline(true);
        aiModel.setIsDeleted(false);
        aiModel.setDetectionTime(LocalDateTime.now());
        return aiChatModelsMapper.insert(aiModel);
    }

}
