package cn.aitenry.iims.ai.agent.react.exception;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
public class MaxIterationReachedException extends Exception {

    public MaxIterationReachedException() {
        super();
    }

    public MaxIterationReachedException(String message) {
        super(message);
    }

    public MaxIterationReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaxIterationReachedException(Throwable cause) {
        super(cause);
    }

    protected MaxIterationReachedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
