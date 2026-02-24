package cn.aitenry.iims.ai.chat.model.entity;

import cn.aitenry.iims.ai.rag.enums.FileModelTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelUseInfo {

    private List<String> context;

    private FileModelTypeEnum type;

}
