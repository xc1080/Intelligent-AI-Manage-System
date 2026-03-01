package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Wiki;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import cn.aitenry.iims.integral.model.dto.wiki.FindWikiPageListDTO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
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

    Wiki selectMaxWeight();

}
