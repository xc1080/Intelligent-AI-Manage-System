package cn.aitenry.iims.integral.controller;

import cn.aitenry.iims.common.result.Result;
import cn.aitenry.iims.integral.model.vo.statistics.StatisticsDataVO;
import cn.aitenry.iims.integral.model.vo.statistics.StatisticsDayDataVO;
import cn.aitenry.iims.integral.service.StatisticsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@RestController
@RequestMapping("/iims/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/data")
    @ApiOperation(value = "获取数据统计")
    public Result<StatisticsDataVO> getStatisticsData() {
        return Result.success(statisticsService.getStatisticsData());
    }

    @GetMapping("/day/data")
    public Result<StatisticsDayDataVO> getStatisticsDayData() {
        return Result.success(statisticsService.getStatisticsDayData());
    }

}
