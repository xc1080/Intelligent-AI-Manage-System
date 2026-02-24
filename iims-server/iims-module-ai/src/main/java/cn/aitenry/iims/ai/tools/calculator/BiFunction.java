package cn.aitenry.iims.ai.tools.calculator;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
interface BiFunction<T, U, R> {
    R apply(T t, U u);
}