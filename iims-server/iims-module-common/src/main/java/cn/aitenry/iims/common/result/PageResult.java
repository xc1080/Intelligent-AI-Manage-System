package cn.aitenry.iims.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@NoArgsConstructor
public class PageResult implements Serializable {

    private long total;

    private List list;

    public PageResult(long total, List list) {
        this.total = total;
        this.list = list == null ? Collections.emptyList() : list;
    }
}
