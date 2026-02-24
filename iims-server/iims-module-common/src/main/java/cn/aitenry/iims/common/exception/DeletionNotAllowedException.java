package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public class DeletionNotAllowedException extends BaseException {

    public DeletionNotAllowedException(String msg) {
        super(msg);
    }

}
