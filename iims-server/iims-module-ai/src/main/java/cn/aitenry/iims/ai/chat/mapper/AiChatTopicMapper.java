package cn.aitenry.iims.ai.chat.mapper;

import com.github.pagehelper.Page;
import cn.aitenry.iims.ai.chat.model.entity.AiChatTopic;
import cn.aitenry.iims.ai.chat.model.vo.ChatTopicVO;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiChatTopicMapper {

    Page<ChatTopicVO> pageQuery(AiChatTopic aiChatTopic);

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(AiChatTopic aiChatTopic);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateTopicDeleted(DeletedStatus deletedStatus);

    @AutoFill(value = OperationType.UPDATE)
    Boolean update(AiChatTopic aiChatTopic);

}
