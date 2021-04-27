package com.yingf.aspect;

import com.yingf.annotation.DataSource;
import com.yingf.datasource.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 4/25/21 5:46 PM
 */
@Aspect
public class DataSourceAspect {

    private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

    @Pointcut("@annotation(com.yingf.annotation.DataSource)"
            + "|| @within(com.yingf.annotation.DataSource)")
    public void doPointCut() {
    }

    @Around("doPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource dataSource = getDataSource(point);

        if (!StringUtils.isEmpty(dataSource.value().name())) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }

        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceContextHolder.remove();
        }


    }

    /**
     * 获取需要切换的数据源
     */
    public DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        // 判断类上是否存在 DataSource注解
        Class<?> targetClass = point.getTarget().getClass();
        DataSource targetDataSource = targetClass.getAnnotation(DataSource.class);
        if (!StringUtils.isEmpty(targetDataSource)) {
            return targetDataSource;
        } else {
            // 若类上不存在, 则判断方法上是否存在 DataSource注解
            Method method = signature.getMethod();
            return method.getAnnotation(DataSource.class);
        }
    }
}
