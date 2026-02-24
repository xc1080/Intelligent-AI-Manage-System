package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 密码修改失败异常
 **/
public class PasswordEditFailedException extends BaseException{

    public PasswordEditFailedException(String msg){
        super(msg);
    }

}
