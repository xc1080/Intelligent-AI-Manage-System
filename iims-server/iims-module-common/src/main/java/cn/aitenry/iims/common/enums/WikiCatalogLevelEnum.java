package cn.aitenry.iims.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Getter
@AllArgsConstructor
public enum WikiCatalogLevelEnum {

    // 一级目录
    ONE(1),
    // 二级目录
    TWO(2);

    private final Integer value;

}
