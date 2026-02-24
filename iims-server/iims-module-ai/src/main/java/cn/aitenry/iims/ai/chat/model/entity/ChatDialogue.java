package cn.aitenry.iims.ai.chat.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "分页查询返回的数据格式")
@Builder
public class ChatDialogue implements Serializable {


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long lastId;

    private String sender;

    private String content;

    private List<Long> fileIds;

    private String metadata;

    private String tools;

    private Boolean isStar;

    private Integer feedbackStatus;

}
