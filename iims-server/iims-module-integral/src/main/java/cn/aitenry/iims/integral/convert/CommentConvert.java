package cn.aitenry.iims.integral.convert;

import cn.aitenry.iims.common.model.entity.integral.Comment;
import cn.aitenry.iims.integral.model.vo.comment.FindCommentItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface CommentConvert {
    /**
     * 初始化 convert 实例
     */
    CommentConvert INSTANCE = Mappers.getMapper(CommentConvert.class);

    FindCommentItemVO convertDO2VO(Comment bean);

}
