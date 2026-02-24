package cn.aitenry.iims.ai.chat.service;

import com.github.pagehelper.Page;
import cn.aitenry.iims.ai.chat.model.dto.ModelPageQueryDTO;
import cn.aitenry.iims.ai.chat.model.entity.AiModel;
import cn.aitenry.iims.ai.chat.model.entity.ChatApi;
import cn.aitenry.iims.ai.chat.model.vo.ModelVO;
import cn.aitenry.iims.ai.chat.model.vo.SelectModelVO;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface AiChatModelsService {

    ChatApi selectModelById(Long id);

    Page<ModelVO> pageQuery(ModelPageQueryDTO modelPageQueryDTO);

    List<SelectModelVO> selectModelList();

    boolean deleteModelById(Long id);

    boolean updateModel(AiModel aiModel);

    boolean insertModel(AiModel aiModel);
}
