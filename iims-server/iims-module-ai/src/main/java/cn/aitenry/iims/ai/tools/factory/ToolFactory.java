package cn.aitenry.iims.ai.tools.factory;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Component
public class ToolFactory {
    
    private final List<AITool> availableTools;
    
    public ToolFactory(List<AITool> tools) {
        this.availableTools = tools;
    }
    
    public List<Object> getEnabledToolInstances() {
        return availableTools.stream()
                .filter(AITool::isEnabled)
                .map(AITool::getToolInstance)
                .collect(Collectors.toList());
    }

    public List<AITool> getAllTools() {
        return availableTools;
    }
    
    public List<String> getAvailableToolNames() {
        return availableTools.stream()
                .filter(AITool::isEnabled)
                .map(AITool::getToolName)
                .collect(Collectors.toList());
    }

}