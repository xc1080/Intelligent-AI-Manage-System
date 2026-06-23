package cn.aitenry.iims.ai.graph.service;

import cn.aitenry.iims.ai.graph.model.entity.GraphEntity;
import cn.aitenry.iims.ai.graph.model.entity.GraphRelationship;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public interface GenerateGraphService {

    List<GraphEntity> extractEntityByModel(String chunk);

    List<GraphRelationship> generateRelationshipByModel(String chunk, String content);

}
