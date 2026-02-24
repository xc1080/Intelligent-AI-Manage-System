package cn.aitenry.iims.common.enums;

import lombok.Getter;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Getter
public enum IntegralDictType {
    CATEGORY(0L),
    TAG(1L);

    private final Long dictId;

    IntegralDictType(Long dictId) {
        this.dictId = dictId;
    }

}
