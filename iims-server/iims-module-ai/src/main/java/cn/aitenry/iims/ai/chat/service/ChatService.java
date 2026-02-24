package cn.aitenry.iims.ai.chat.service;

import cn.aitenry.iims.ai.chat.model.dto.SendMessageDTO;
import cn.aitenry.iims.ai.chat.model.vo.SelectEndpointVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatService {

    SseEmitter conversation(Long uuid, SendMessageDTO messageDto);

    SseEmitter regenerate(Long uuid, Long lastId);

    Boolean stopConversation(Long uuid);

    List<SelectEndpointVO> selectEndpointList();
}
