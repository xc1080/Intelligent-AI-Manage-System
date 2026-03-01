package cn.aitenry.iims.ai.chat.mapper;


import cn.aitenry.iims.ai.chat.model.dto.ModelPageQueryDTO;
import cn.aitenry.iims.ai.chat.model.entity.AiModel;
import cn.aitenry.iims.ai.chat.model.entity.ChatApi;
import cn.aitenry.iims.ai.chat.model.vo.ModelVO;
import cn.aitenry.iims.ai.chat.model.vo.SelectModelVO;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiChatModelsMapper {

    ChatApi selectModelById(Long id);

    Page<ModelVO> pageQuery(ModelPageQueryDTO modelPageQueryDTO);

    List<SelectModelVO> selectModelList();

    Boolean deleteModelById(Long id);

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(AiModel aiModel);

    @AutoFill(value = OperationType.UPDATE)
    Boolean update(AiModel aiModel);
}
