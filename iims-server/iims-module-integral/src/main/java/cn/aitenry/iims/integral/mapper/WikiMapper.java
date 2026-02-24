package cn.aitenry.iims.integral.mapper;

import com.github.pagehelper.Page;
import cn.aitenry.iims.integral.model.dto.wiki.FindWikiPageListDTO;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Wiki;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface WikiMapper {

    /**
     * 新增知识库
     *
     * @param wiki Wiki
     */
    @AutoFill(value = OperationType.INSERT)
    int insert(Wiki wiki);

    @AutoFill(value = OperationType.UPDATE)
    int deleteById(DeletedStatus deletedStatus);

    Page<Wiki> pageQuery(FindWikiPageListDTO dto, Boolean openPublish);

    @AutoFill(value = OperationType.UPDATE)
    void updateById(Wiki wiki);

    @Select("select * from iims_integral_wiki order by weight desc limit 1")
    Wiki selectMaxWeight();

}
