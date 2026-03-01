package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface CommentMapper {

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(Comment comment);

    List<Comment> selectByRouterUrlAndStatus(String routerUrl, Integer status);
}
