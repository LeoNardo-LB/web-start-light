package org.smm.archetype.util.log;

import org.smm.archetype.util.log.handler.persistence.PersistenceType;
import org.smm.archetype.util.log.handler.stringify.StringifyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Leonardo
 * @since 2025/7/15
 * 业务日志注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BizLog {
    
    /**
     * 业务名称
     */
    String value() default "";
    
    /**
     * 持久化类型
     */
    PersistenceType[] persistence() default PersistenceType.DB;
    
    /**
     * 默认持久化类型下转为字符串的形式
     */
    StringifyType stringify() default StringifyType.JDK;
    
}
