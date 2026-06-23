package cn.aitenry.iims.ai.graph.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphRelationship {

    private Long id;

    private Long from;

    private Long to;

    private Integer weight;

    private String relationship;

    private String description;

    private List<String> keywords;

}
