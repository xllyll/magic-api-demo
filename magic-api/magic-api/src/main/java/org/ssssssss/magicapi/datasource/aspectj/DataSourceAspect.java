package org.ssssssss.magicapi.datasource.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.datasource.annotation.TargetDataSource;
import org.ssssssss.magicapi.datasource.context.DynamicDataSourceContextHolder;

import java.util.Objects;

/**
 * 多数据源处理
 * 
 * @author xllyll
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(org.ssssssss.magicapi.datasource.annotation.TargetDataSource)"
            + "|| @within(org.ssssssss.magicapi.datasource.annotation.TargetDataSource)")
    public void dsPointCut()
    {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable
    {
        TargetDataSource dataSource = getDataSource(point);

        if (dataSource!=null)
        {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value());
        }

        try
        {
            return point.proceed();
        }
        finally
        {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 获取需要切换的数据源
     */
    public TargetDataSource getDataSource(ProceedingJoinPoint point)
    {
        MethodSignature signature = (MethodSignature) point.getSignature();
        TargetDataSource dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), TargetDataSource.class);
        if (Objects.nonNull(dataSource))
        {
            return dataSource;
        }

        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), TargetDataSource.class);
    }
}
