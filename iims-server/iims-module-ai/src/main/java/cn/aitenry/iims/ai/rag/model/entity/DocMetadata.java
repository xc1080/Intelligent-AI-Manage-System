package cn.aitenry.iims.ai.rag.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class DocMetadata implements Serializable {

    private String id;

    private String text;

    private Float score;

    private Metadata metadata;

}
