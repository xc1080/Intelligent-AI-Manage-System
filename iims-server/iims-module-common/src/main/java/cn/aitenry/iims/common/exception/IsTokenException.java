package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 判断token是否存在异常
 **/
public class IsTokenException extends BaseException {

    public IsTokenException() {
    }

    public IsTokenException(String msg) {
        super(msg);
    }

}
