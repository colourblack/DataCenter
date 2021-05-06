package com.yingf.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 5/6/21 10:30 AM
 */
public class ApplicationContextUtil implements ApplicationContextAware {

    protected static ConfigurableApplicationContext context;

    /**
     * web 容器中进行部署, 使用此方法获取上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.context = (ConfigurableApplicationContext) applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 根据Bean Name获取Bean
     *
     * @param beanName 容器中bean的名称
     * @return 容器中对应bean的实例
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clazz){
        return context.getBean(beanName, clazz);
    }
}