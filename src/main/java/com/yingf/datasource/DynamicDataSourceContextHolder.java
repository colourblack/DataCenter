package com.yingf.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 4/25/21 4:58 PM
 */
public class DynamicDataSourceContextHolder {

    public static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    private static final ThreadLocal<String> DATASOURCE_CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前数据源
     * @param dataSourceType 当前数据源名称
     */
    public static void setDataSourceType(String dataSourceType) {
        log.info("设置当前数据源为: {}", dataSourceType);
        DATASOURCE_CONTEXT_HOLDER.set(dataSourceType);
    }

    /**
     * 获取当前数据源名称
     * @return 当前数据源名称
     */
    public static String getDataSourceType() {
        return DATASOURCE_CONTEXT_HOLDER.get();
    }

    /**
     * 删除当前数据源信息
     */
    public static void remove() {
        DATASOURCE_CONTEXT_HOLDER.remove();
    }

}
