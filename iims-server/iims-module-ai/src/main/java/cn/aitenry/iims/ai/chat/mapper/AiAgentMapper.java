package cn.aitenry.iims.ai.chat.mapper;

import cn.aitenry.iims.ai.chat.model.vo.SelectAgentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2026/2/9 19:50
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface AiAgentMapper {

    List<SelectAgentVO> selectAgentList();

}
