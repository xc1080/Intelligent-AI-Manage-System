package cn.aitenry.iims.common.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0

 **/
public class ResultUtil {

    public static Map<String, Object> filterNonNullProperties(Object object) {
        Map<String, Object> nonNullProperties = new HashMap<>();
        if (object != null) {
            Class<?> clazz = object.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // 设置为可访问私有字段
                try {
                    Object value = field.get(object);
                    if (value != null) { // 只处理非空值
                        nonNullProperties.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return nonNullProperties;
    }
}
