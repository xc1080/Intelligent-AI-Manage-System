package cn.aitenry.iims.ai.chat.service;

import cn.aitenry.iims.ai.chat.model.dto.ChatRenameTopicDTO;
import cn.aitenry.iims.ai.chat.model.dto.ChatTopicPageQueryDTO;
import cn.aitenry.iims.ai.chat.model.entity.AiChatTopic;
import cn.aitenry.iims.common.result.PageResult;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface TopicManageService {
    PageResult chatTopicPageQuery(ChatTopicPageQueryDTO topicPageQueryDto);

    void insertTopic(AiChatTopic aiChatTopic);

    Boolean delTopic(List<Long> ids);

    Boolean renameTopic(ChatRenameTopicDTO renameTopicDto);
}
