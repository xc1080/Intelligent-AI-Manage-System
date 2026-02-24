package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 账号被锁定异常
 **/
public class AccountLockedException extends BaseException {

    public AccountLockedException() {
    }

    public AccountLockedException(String msg) {
        super(msg);
    }

}
