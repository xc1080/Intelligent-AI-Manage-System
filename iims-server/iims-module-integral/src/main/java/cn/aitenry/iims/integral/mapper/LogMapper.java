package cn.aitenry.iims.integral.mapper;

import com.github.pagehelper.Page;
import cn.aitenry.iims.integral.model.dto.log.LogPageQueryDTO;
import cn.aitenry.iims.integral.model.vo.log.LogVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 15:28
 * @Version: v1.0.0

 **/
@Mapper
public interface LogMapper {

    Page<LogVO> pageQuery(LogPageQueryDTO logPageQueryDTO);

}
