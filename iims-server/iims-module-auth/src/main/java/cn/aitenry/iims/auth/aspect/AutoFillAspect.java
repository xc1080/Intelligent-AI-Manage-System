package cn.aitenry.iims.auth.aspect;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.constant.AutoFillConstant;
import cn.aitenry.iims.common.context.BaseContext;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.utils.SnowFlakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公共字段自动填充切面
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    private final SnowFlakeIdWorker snowFlakeIdWorker;

    public AutoFillAspect(SnowFlakeIdWorker snowFlakeIdWorker) {
        this.snowFlakeIdWorker = snowFlakeIdWorker;
    }

    @Pointcut("@annotation(cn.aitenry.iims.common.annotation.AutoFill)")
    public void autoFillPointCut() {}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        log.debug("{} >> {} >> 执行公共字段自动填充", signature.getDeclaringTypeName(), method.getName());

        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        if (autoFill == null) {
            return;
        }

        OperationType operationType = autoFill.value();
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            log.warn("自动填充失败：方法参数为空");
            return;
        }

        Object entity = args[0];
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        try {
            if (operationType == OperationType.INSERT) {
                fillInsertFields(entity, now, currentId);
            } else if (operationType == OperationType.UPDATE) {
                fillUpdateFields(entity, now, currentId);
            }
        } catch (Exception e) {
            log.error("自动填充字段时发生异常", e);
        }
    }

    private void fillInsertFields(Object entity, LocalDateTime now, Long currentId) {
        if (entity instanceof List<?>) {
            ((List<?>) entity).forEach(e -> fillInsertFields(e, now, currentId));
            return;
        }

        try {
            // 尝试填充ID（部分实体可能使用数据库自增，无setId方法）
            try {
                Method setId = entity.getClass().getMethod(AutoFillConstant.SET_ID, Long.class);
                setId.invoke(entity, snowFlakeIdWorker.nextId());
                log.trace("已填充ID: {}", entity.getClass().getSimpleName());
            } catch (NoSuchMethodException e) {
                log.trace("实体 {} 无setId方法，跳过ID填充（可能使用数据库自增）",
                        entity.getClass().getSimpleName());
            }

            // 填充时间字段
            Method setCreateTime = entity.getClass()
                    .getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = entity.getClass()
                    .getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            setCreateTime.invoke(entity, now);
            setUpdateTime.invoke(entity, now);

            // 填充操作人字段（仅当上下文存在用户ID时）
            if (currentId != null) {
                try {
                    Method setCreateBy = entity.getClass()
                            .getMethod(AutoFillConstant.SET_CREATE_BY, Long.class);
                    Method setUpdateBy = entity.getClass()
                            .getMethod(AutoFillConstant.SET_UPDATE_BY, Long.class);
                    setCreateBy.invoke(entity, currentId);
                    setUpdateBy.invoke(entity, currentId);
                } catch (NoSuchMethodException e) {
                    log.trace("实体 {} 无操作人字段，跳过填充", entity.getClass().getSimpleName());
                }
            }

            log.debug("INSERT操作自动填充完成: {}", entity.getClass().getSimpleName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("填充INSERT字段失败，实体类型: {}", entity.getClass().getName(), e);
            throw new RuntimeException("自动填充失败，请检查实体类是否包含必要setter方法", e);
        }
    }

    private void fillUpdateFields(Object entity, LocalDateTime now, Long currentId) {
        if (entity instanceof List<?>) {
            ((List<?>) entity).forEach(e -> fillUpdateFields(e, now, currentId));
            return;
        }

        try {
            Method setUpdateTime = entity.getClass()
                    .getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            setUpdateTime.invoke(entity, now);

            if (currentId != null) {
                try {
                    Method setUpdateBy = entity.getClass()
                            .getMethod(AutoFillConstant.SET_UPDATE_BY, Long.class);
                    setUpdateBy.invoke(entity, currentId);
                } catch (NoSuchMethodException e) {
                    log.trace("实体 {} 无updateBy字段，跳过填充", entity.getClass().getSimpleName());
                }
            }

            log.debug("UPDATE操作自动填充完成: {}", entity.getClass().getSimpleName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("填充UPDATE字段失败，实体类型: {}", entity.getClass().getName(), e);
            throw new RuntimeException("自动填充失败，请检查实体类是否包含必要setter方法", e);
        }
    }
}