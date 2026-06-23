package cn.aitenry.iims.ai.agent.react.event;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public interface ReActAgentEvent {
    /**
     * 获取事件类型名称
     */
    default String getType() {
        return this.getClass().getSimpleName();
    }

    default Boolean getComplete() {
        return true;
    }

    /**
     * 获取事件内容
     */
    Object getContent();
}