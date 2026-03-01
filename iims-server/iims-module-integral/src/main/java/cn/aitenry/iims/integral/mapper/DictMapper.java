package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Dict;
import cn.aitenry.iims.common.model.entity.integral.DictValue;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import cn.aitenry.iims.common.model.entity.status.DisableStatus;
import cn.aitenry.iims.integral.model.dto.dict.DictPageQueryDTO;
import cn.aitenry.iims.integral.model.dto.dict.DictValuePageQueryDTO;
import cn.aitenry.iims.integral.model.vo.dict.DictVO;
import cn.aitenry.iims.integral.model.vo.dict.DictValueVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface DictMapper {

    @AutoFill(value = OperationType.INSERT)
    Boolean insertDict(Dict dict);

    @AutoFill(value = OperationType.INSERT)
    Boolean insertDictValue(DictValue dictValue);

    Page<DictVO> dictPageQuery(DictPageQueryDTO dictPageQueryDto);

    Page<DictValueVO> dictValuePageQuery(Long dictId, DictValuePageQueryDTO dictValuePageQueryDto);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateDictDeleted(DeletedStatus deletedStatus);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateDictValueDeleted(DeletedStatus deletedStatus);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateDict(Dict dict);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateDictValue(DictValue dictValue);

    List<DictValueVO> getDictValueListByDictId(Long dictId);

    DictValue getDictValueById(Long id);

    List<DictValue> getDictValueByIds(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    Boolean disableDict(DisableStatus disable);

}
