package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 业务异常
 **/
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
