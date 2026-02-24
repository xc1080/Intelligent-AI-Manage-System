package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 密码错误异常
 **/
public class PasswordErrorException extends BaseException {

    public PasswordErrorException() {
    }

    public PasswordErrorException(String msg) {
        super(msg);
    }

}
