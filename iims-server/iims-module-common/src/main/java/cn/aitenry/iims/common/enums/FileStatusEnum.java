package cn.aitenry.iims.common.enums;

import lombok.Getter;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
@Getter
public enum FileStatusEnum {

    DELETE(-1),
    NOT_USED(0),
    USED(1);

    private final Integer code;

    FileStatusEnum(Integer code) {
        this.code = code;
    }

}
