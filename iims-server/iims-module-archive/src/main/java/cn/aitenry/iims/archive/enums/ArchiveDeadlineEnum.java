package cn.aitenry.iims.archive.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Getter
public enum ArchiveDeadlineEnum {

    PERMANENT("999", "永久", "Y"),
    REGULARLY_FOR_TEN_YEARS("10", "定期10年", "D10"),
    REGULARLY_FOR_THIRTY_YEARS("30", "定期30年", "D30");

    private final String key;
    private final String info;
    private final String code;

    ArchiveDeadlineEnum(String key, String info, String code) {
        this.key = key;
        this.info = info;
        this.code = code;
    }

    public static ArchiveDeadlineEnum fromKey(String key) {
        for (ArchiveDeadlineEnum level : values()) {
            if (Objects.equals(level.getKey(), key)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid key: " + key);
    }

}
