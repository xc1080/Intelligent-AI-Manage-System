package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.integral.model.vo.statistics.StatisticsDataVO;
import cn.aitenry.iims.integral.model.vo.statistics.StatisticsDayDataVO;

import java.time.LocalDate;

public interface StatisticsService {

    StatisticsDataVO getStatisticsData();

    StatisticsDayDataVO getStatisticsDayData();

    void generateDailyStatistics(LocalDate date);

}
