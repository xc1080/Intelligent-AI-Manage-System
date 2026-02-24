package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 数据不存在异常
 **/
public class NotFoundException extends BaseException {

    public NotFoundException() {
    }

    public NotFoundException(String msg) {
        super(msg);
    }

}
