package cn.aitenry.iims.archive.mapper;

import cn.aitenry.iims.archive.model.entity.ArchiveMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Aitenry
 * @Date: 2025/11/16 12:54
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface ArchiveHashMapper {

    ArchiveMapper getMapperById(Long id);

}
