package cn.aitenry.iims.ai.chat.controller;

import cn.aitenry.iims.ai.chat.model.dto.SendMessageDTO;
import cn.aitenry.iims.ai.chat.model.vo.SelectEndpointVO;
import cn.aitenry.iims.ai.chat.service.ChatService;
import cn.aitenry.iims.common.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Slf4j
@RestController
@RequestMapping("/iims/ai/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService ChatService) {
        this.chatService = ChatService;
    }

    /**
     * 发送消息
     * @param uuid 唯一标识符号
     * @return SseEmitter
     */
    @ApiOperation("回答用户问题")
    @PostMapping(value = "/receive/answer/{uuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter conversation(@PathVariable("uuid") Long uuid, @RequestBody SendMessageDTO messageDto) {
        return chatService.conversation(uuid, messageDto);
    }

    @GetMapping("/endpoint/list")
    public Result<List<SelectEndpointVO>> selectEndpointList() {
        return Result.success(chatService.selectEndpointList());
    }

    @ApiOperation("重新生成回答")
    @PostMapping(value = "/regenerate/answer/{uuid}/{lastId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter regenerate(@PathVariable("uuid") Long uuid, @PathVariable("lastId") Long lastId) {
        return chatService.regenerate(uuid, lastId);
    }

    @ApiOperation("停止回答用户问题")
    @GetMapping("/stop/answer/{uuid}")
    public Result<T> stopConversation(@PathVariable("uuid") Long uuid) {
        return Result.fromBoolean(chatService.stopConversation(uuid));
    }

}