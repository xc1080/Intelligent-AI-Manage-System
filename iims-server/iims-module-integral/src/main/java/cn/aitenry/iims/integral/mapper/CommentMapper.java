package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    @AutoFill(value = OperationType.INSERT)
    Boolean insert(Comment comment);

    @Select("SELECT * FROM iims_integral_comment WHERE router_url = #{routerUrl} AND status = #{status} ORDER BY create_time DESC;")
    List<Comment> selectByRouterUrlAndStatus(String routerUrl, Integer status);
}
