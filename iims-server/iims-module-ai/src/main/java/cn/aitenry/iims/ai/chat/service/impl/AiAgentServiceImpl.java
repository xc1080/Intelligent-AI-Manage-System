package cn.aitenry.iims.ai.chat.service.impl;

import cn.aitenry.iims.ai.chat.mapper.AiAgentMapper;
import cn.aitenry.iims.ai.chat.model.vo.SelectAgentVO;
import cn.aitenry.iims.ai.chat.service.AiAgentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2026/2/9 19:58
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class AiAgentServiceImpl implements AiAgentService {

    private final AiAgentMapper aiAgentMapper;

    public AiAgentServiceImpl(AiAgentMapper aiAgentMapper) {
        this.aiAgentMapper = aiAgentMapper;
    }

    @Override
    public List<SelectAgentVO> selectAgentList() {
        return aiAgentMapper.selectAgentList();
    }
}
