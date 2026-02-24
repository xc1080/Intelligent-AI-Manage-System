package cn.aitenry.iims.common.mapper;

import cn.aitenry.iims.common.model.entity.log.Logs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogsMapper {

    void insert(Logs log);

}
