package cn.aitenry.iims.common.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 字段不能为空异常
 **/
public class FieldCannotEmptyException extends BaseException {

    public FieldCannotEmptyException(String msg) {
        super(msg);
    }

}
