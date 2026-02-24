package cn.aitenry.iims.integral.convert;

import cn.aitenry.iims.integral.model.vo.wiki.FindWikiPageListVO;
import cn.aitenry.iims.common.model.entity.integral.Wiki;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface WikiConvert {
    /**
     * 初始化 convert 实例
     */
    WikiConvert INSTANCE = Mappers.getMapper(WikiConvert.class);

    /**
     * WikiDO -> FindWikiPageListDto
     */
    @Mapping(target = "isTop", expression = "java(bean.getWeight() > 0)")
    FindWikiPageListVO convertDO2VO(Wiki bean);

}
