package cn.aitenry.iims.ai.chat.service;

import cn.aitenry.iims.ai.chat.model.dto.ChatRenameTopicDTO;
import cn.aitenry.iims.ai.chat.model.dto.ChatTopicPageQueryDTO;
import cn.aitenry.iims.ai.chat.model.entity.AiChatTopic;
import cn.aitenry.iims.common.result.PageResult;

import java.util.List;

public interface TopicManageService {
    PageResult chatTopicPageQuery(ChatTopicPageQueryDTO topicPageQueryDto);

    void insertTopic(AiChatTopic aiChatTopic);

    Boolean delTopic(List<Long> ids);

    Boolean renameTopic(ChatRenameTopicDTO renameTopicDto);
}
