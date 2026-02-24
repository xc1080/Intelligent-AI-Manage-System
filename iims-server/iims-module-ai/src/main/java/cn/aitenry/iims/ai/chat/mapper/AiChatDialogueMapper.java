package cn.aitenry.iims.ai.chat.mapper;

import com.github.pagehelper.Page;
import cn.aitenry.iims.ai.chat.model.entity.AiChatDialogue;
import cn.aitenry.iims.ai.chat.model.entity.ChatDialogue;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiChatDialogueMapper {

    Page<ChatDialogue> pageQuery(AiChatDialogue aiChatDialogue);

    List<ChatDialogue> getHistoryByTopicId(Long topicId, Integer size);

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(AiChatDialogue aiChatDialogue);

    @AutoFill(value = OperationType.UPDATE)
    Boolean update(AiChatDialogue aiChatDialogue);

    @AutoFill(value = OperationType.UPDATE)
    Boolean del(AiChatDialogue aiChatDialogue);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateDeletedByTopicIds(DeletedStatus deletedStatus);

}
