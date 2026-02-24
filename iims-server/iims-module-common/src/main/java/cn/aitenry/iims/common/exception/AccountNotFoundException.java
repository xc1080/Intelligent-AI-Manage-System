package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 账号不存在异常
 **/
public class AccountNotFoundException extends BaseException {

    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
