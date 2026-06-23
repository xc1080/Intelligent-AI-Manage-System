package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.integral.model.dto.comment.*;
import cn.aitenry.iims.integral.model.vo.comment.FindCommentListVO;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public interface CommentService {

    /**
     * 查询评论分页数据
     */
    PageResult findCommentPageList(FindCommentPageListDTO dto);

    /**
     * 删除评论
     */
    Boolean deleteComment(DeleteCommentDTO dto);

    /**
     * 评论审核
     */
    Boolean examine(ExamineCommentDTO dto);

    /**
     * 发布评论
     */
    Boolean publishComment(PublishCommentDTO dto);

    /**
     * 查询页面所有评论
     */
    FindCommentListVO findCommentList(FindCommentListDTO dto);

}
