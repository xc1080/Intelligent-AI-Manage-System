package cn.aitenry.iims.common.model.entity.integral;

import cn.aitenry.iims.common.model.entity.base.BaseTable;
import lombok.*;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseTable {

    private Long id;

    private String content;

    private String routerUrl;

    private Boolean isDeleted;

    private Long replyCommentId;

    private Long parentCommentId;

    private Integer status;

    private String reason;
}