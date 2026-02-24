package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.integral.model.vo.statistics.StatisticsDataVO;
import cn.aitenry.iims.integral.model.vo.statistics.StatisticsDayDataVO;

import java.time.LocalDate;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface StatisticsService {

    StatisticsDataVO getStatisticsData();

    StatisticsDayDataVO getStatisticsDayData();

    void generateDailyStatistics(LocalDate date);

}
