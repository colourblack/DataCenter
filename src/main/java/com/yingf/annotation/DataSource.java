package com.yingf.annotation;

import com.yingf.enums.DataSourceType;

import java.lang.annotation.*;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 4/25/21 5:44 PM
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
    /**
     * 切换数据源名称
     */
    DataSourceType value() default DataSourceType.MASTER;

}
