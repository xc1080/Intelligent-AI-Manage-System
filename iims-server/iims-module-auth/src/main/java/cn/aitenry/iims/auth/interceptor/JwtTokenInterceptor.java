package cn.aitenry.iims.auth.interceptor;

import cn.aitenry.iims.common.model.entity.integral.User;
import cn.dev33.satoken.stp.StpUtil;
import cn.aitenry.iims.common.context.BaseContext;
import cn.aitenry.iims.common.exception.IsTokenException;
import cn.aitenry.iims.common.properties.JwtProperties;
import cn.aitenry.iims.integral.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: jwt令牌校验的拦截器
 **/
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final UserService userService;

    public JwtTokenInterceptor(JwtProperties jwtProperties, UserService userService) {
        this.jwtProperties = jwtProperties;
        this.userService = userService;
    }

    /**
     * 校验jwt
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler Object
     * @return boolean
     */
    public boolean preHandle(
            @NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull Object handler) {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());
        if (token == null) throw new IsTokenException("Token不存在");

        // 校验令牌
        try {
            long id = StpUtil.getLoginIdAsLong();
            User user = userService.getById(id);
            if (user.getIsDisable())
                throw new IsTokenException("账号锁定");
            if (user.getIsDeleted())
                throw new IsTokenException("账号已被移除");
            // 将用户id存储到ThreadLocal
            BaseContext.setCurrentId(id);
            // 通过，放行
            return true;
        } catch (Exception ex) {
            throw new IsTokenException(ex.getMessage());
        }
    }
}
