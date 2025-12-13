package org.smm.archetype.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Leonardo
 * @since 2025/7/15
 * Spring 上下文工具类
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
    
    public static ApplicationContext context;
    
    public static Object getBean(String name) {
        return context.getBean(name);
    }
    
    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }
    
    public static <T> T getBean(String name, Class<T> requiredType) {
        return context.getBean(name, requiredType);
    }
    
    public static boolean containsBean(String name) {
        return context.containsBean(name);
    }
    
    public static boolean isSingleton(String name) {
        return context.isSingleton(name);
    }
    
    public static Class<?> getType(String name) {
        return context.getType(name);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.context = applicationContext;
    }
    
}