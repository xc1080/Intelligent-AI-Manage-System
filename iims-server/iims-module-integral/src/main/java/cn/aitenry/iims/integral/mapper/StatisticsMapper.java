package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.integral.model.entity.StatisticsEntity;
import cn.aitenry.iims.integral.model.vo.statistics.StatisticsDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface StatisticsMapper {

    StatisticsDataVO getStatisticsData();

    /**
     * 根据日期范围统计每日数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return StatisticsDataVO
     */
    StatisticsDataVO getStatisticsDataByDay(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * 插入统计数据
     * @param statisticsEntity 统计数据实体
     * @return Boolean
     */
    Boolean insertStatisticsData(StatisticsEntity statisticsEntity);

    /**
     * 根据日期查询已存在的统计数据
     * @param date 日期
     * @return StatisticsEntity
     */
    StatisticsEntity getStatisticsDataByDate(@Param("date") LocalDate date);

    /**
     * 查询统计数据
     * @return List
     */
    List<StatisticsEntity> getStatisticsAllData();

    /**
     * 更新统计数据
     * @param statisticsEntity 统计数据实体
     * @return Boolean
     */
    Boolean updateStatisticsData(StatisticsEntity statisticsEntity);
}