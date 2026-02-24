package cn.aitenry.iims.ai.chat.mapper;


import com.github.pagehelper.Page;
import cn.aitenry.iims.ai.chat.model.dto.ModelPageQueryDTO;
import cn.aitenry.iims.ai.chat.model.entity.AiModel;
import cn.aitenry.iims.ai.chat.model.entity.ChatApi;
import cn.aitenry.iims.ai.chat.model.vo.ModelVO;
import cn.aitenry.iims.ai.chat.model.vo.SelectModelVO;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiChatModelsMapper {

    @Select("select * from iims_ai_chat_models where id = #{id} limit 1")
    ChatApi selectModelById(Long id);

    Page<ModelVO> pageQuery(ModelPageQueryDTO modelPageQueryDTO);

    List<SelectModelVO> selectModelList();

    Boolean deleteModelById(Long id);

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(AiModel aiModel);

    @AutoFill(value = OperationType.UPDATE)
    Boolean update(AiModel aiModel);
}
