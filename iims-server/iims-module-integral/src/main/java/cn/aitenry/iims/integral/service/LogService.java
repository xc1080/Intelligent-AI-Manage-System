package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.integral.model.dto.log.LogPageQueryDTO;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 15:06
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface LogService {

    PageResult pageQuery(LogPageQueryDTO logPageQueryDTO);

}
