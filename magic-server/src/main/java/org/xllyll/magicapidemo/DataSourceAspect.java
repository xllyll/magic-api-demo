package org.xllyll.magicapidemo;

import org.springframework.stereotype.Component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Method;

@Aspect
@Component
public class DataSourceAspect {

    @Around("@within(org.xllyll.magicapidemo.TargetDataSource) || @annotation(org.xllyll.magicapidemo.TargetDataSource)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法或类上的 @DataSource 注解
        TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
        if (targetDataSource == null) {
            targetDataSource = joinPoint.getTarget().getClass().getAnnotation(TargetDataSource.class);
        }

        if (targetDataSource != null) {
            System.out.println("切换数据源为：" + targetDataSource.value()); // 调试日志
            // 设置数据源 Key
            DataSourceContextHolder.setDataSourceKey(targetDataSource.value());
        }

        try {
            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            // 清除数据源 Key
//            DataSourceContextHolder.clearDataSourceKey();
        }
    }
}
