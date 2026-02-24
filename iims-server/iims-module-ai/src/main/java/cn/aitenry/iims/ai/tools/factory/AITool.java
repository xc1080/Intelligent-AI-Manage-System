package cn.aitenry.iims.ai.tools.factory;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 抽象工具接口
 **/
public interface AITool {
    /**
     * 获取工具实例
     */
    Object getToolInstance();
    
    /**
     * 获取工具名称
     */
    String getToolName();
    
    /**
     * 判断是否启用该工具
     */
    default boolean isEnabled() {
        return true;
    }
}