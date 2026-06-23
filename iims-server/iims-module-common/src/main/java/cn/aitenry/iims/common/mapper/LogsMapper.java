package cn.aitenry.iims.common.mapper;

import cn.aitenry.iims.common.model.entity.log.Logs;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Mapper
public interface LogsMapper {

    void insert(Logs log);

}
