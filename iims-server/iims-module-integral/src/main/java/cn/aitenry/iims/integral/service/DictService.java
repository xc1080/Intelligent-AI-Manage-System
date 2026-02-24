package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.integral.model.dto.dict.DictDTO;
import cn.aitenry.iims.integral.model.dto.dict.DictPageQueryDTO;
import cn.aitenry.iims.integral.model.dto.dict.DictValueDTO;
import cn.aitenry.iims.integral.model.dto.dict.DictValuePageQueryDTO;
import cn.aitenry.iims.integral.model.vo.dict.DictValueVO;
import cn.aitenry.iims.common.enums.IntegralDictType;
import cn.aitenry.iims.common.model.entity.integral.DictValue;
import cn.aitenry.iims.common.result.PageResult;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface DictService {

    Boolean insertDict(DictDTO dictDto);

    Boolean insertDictValue(DictValueDTO dictValueDto);

    Boolean delDict(List<Long> ids);

    Boolean delDictValue(List<Long> ids);

    Boolean updateDict(DictDTO dictDto);

    Boolean updateDictValue(DictValueDTO dictValueDto);

    PageResult dictPageQuery(DictPageQueryDTO dictPageQueryDto);

    PageResult dictValuePageQuery(Long dictId, DictValuePageQueryDTO dictValuePageQueryDto);

    DictValue getDictValueById(Long id);

    List<DictValue> getDictValueByIds(List<Long> ids);

    List<DictValueVO> getDictValueListByType(IntegralDictType integralDictType);

    Boolean disableDict(Long id, Boolean isDisable);
}
