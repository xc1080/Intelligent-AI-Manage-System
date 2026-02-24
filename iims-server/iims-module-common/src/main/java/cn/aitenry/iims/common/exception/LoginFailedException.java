package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 登录失败
 **/
public class LoginFailedException extends BaseException{
    public LoginFailedException(String msg){
        super(msg);
    }
}
