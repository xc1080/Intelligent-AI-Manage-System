package cn.aitenry.iims.integral.model.vo.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsDayDataVO {

    private List<LocalDate> statisticsDays;

    private List<Integer> dictCounts;

    private List<Integer> fileCounts;

    private List<Integer> logCounts;

    private List<Integer> userCounts;

    private List<Integer> articleCounts;

    private List<Integer> wikiCounts;

}
