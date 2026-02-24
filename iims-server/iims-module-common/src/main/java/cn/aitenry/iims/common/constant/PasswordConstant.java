package cn.aitenry.iims.common.constant;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 密码常量
 **/
public class PasswordConstant {

    private static final String DEFAULT_PASSWORD = "@iims.com";

    public static String resetPassword(String idNumber) {
        return String.format("%s%s", idNumber.substring(idNumber.length() - 6), DEFAULT_PASSWORD);
    }

}
