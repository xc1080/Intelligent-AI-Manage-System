package cn.aitenry.iims.integral.mapper;

import com.github.pagehelper.Page;
import cn.aitenry.iims.integral.model.dto.dict.DictPageQueryDTO;
import cn.aitenry.iims.integral.model.dto.dict.DictValuePageQueryDTO;
import cn.aitenry.iims.integral.model.vo.dict.DictValueVO;
import cn.aitenry.iims.integral.model.vo.dict.DictVO;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Dict;
import cn.aitenry.iims.common.model.entity.integral.DictValue;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import cn.aitenry.iims.common.model.entity.status.DisableStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    @Select("select * from iims_integral_dict_value where dict_id = #{dictId}")
    List<DictValueVO> getDictValueListByDictId(Long dictId);

    @Select("select * from iims_integral_dict_value where id = #{id}")
    DictValue getDictValueById(Long id);

    @Select({
            "<script>",
            "SELECT *",
            "FROM iims_integral_dict_value",
            "WHERE id IN",
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<DictValue> getDictValueByIds(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    Boolean disableDict(DisableStatus disable);

}
