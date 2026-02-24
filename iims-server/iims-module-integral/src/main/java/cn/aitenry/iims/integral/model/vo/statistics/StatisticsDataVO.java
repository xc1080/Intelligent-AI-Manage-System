package cn.aitenry.iims.integral.model.vo.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class StatisticsDataVO {

    private Integer fileCount;

    private Integer logCount;

    private Integer userCount;

    private Integer articleCount;

    private Integer wikiCount;

}
