package cn.aitenry.iims.ai.chat.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.aitenry.iims.ai.chat.model.entity.ChatTool;
import cn.aitenry.iims.ai.chat.model.entity.AiContent;
import cn.aitenry.iims.ai.chat.model.entity.UserContent;
import cn.aitenry.iims.common.model.entity.file.FileInfo;
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
public class ChatDialogueVO implements Serializable {


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long id;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long lastId;

    private String sender;

    private List<AiContent> aiContent;

    private UserContent userContent;

    private List<DocMetadataVO> docMetadata;

    private List<ChatTool> tools;

    private List<FileInfo> fileInfos;

    private Boolean isStar;

    private Integer feedbackStatus;

}
