package cn.aitenry.iims.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Getter
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum WikiTypeEnum {

    /* 企业知识库 */
    ENTERPRISE,

    /* 个人知识库 */
    INDIVIDUAL

}
