package cn.aitenry.iims.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Getter
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum TaskStatusEnum {

    /** 未开始或未完成 */
    NOT_STARTED,

    /** 部分完成 */
    PARTIALLY_COMPLETED,

    /** 已完成 */
    COMPLETED,

    /** 失败 */
    FAILED;

    public static TaskStatusEnum fromCounts(Long count, Long total) {
        if (count == 0) {
            // 如果当前完成数量为0，任务未开始
            return NOT_STARTED;
        }
        if (count < total) {
            // 如果当前完成数量小于总数量，任务部分完成
            return PARTIALLY_COMPLETED;
        }
        if (count.equals(total)) {
            // 如果当前完成数量等于总数量，任务完成
            return COMPLETED;
        }
        return FAILED;
    }

}
