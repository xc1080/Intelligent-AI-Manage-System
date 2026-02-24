package cn.aitenry.iims.integral.model.vo.comment;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCommentListVO {

    /**
     * 总评论数
     */
    private Integer total;

    /**
     * 评论集合
     */
    private List<FindCommentItemVO> comments;

}