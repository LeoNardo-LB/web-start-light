package org.smm.archetype.util;

import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean工具类，提供对象属性操作功能
 * @author Leonardo
 * @since 2025/10/12
 */
@Slf4j
public class MyBeanUtils {

    /**
     * BeanInfo缓存，用于存储Class与其BeanInfo的映射关系
     */
    private static final Map<Class<?>, BeanInfo> BEAN_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 合并对象字段
     * 越往后优先级越高，位于数组后面的对象，属性会覆盖前面的对象的同名属性，返回新的合并后的对象
     * @param targetClass 目标对象类型
     * @param sources     源对象数组
     * @param <T>         目标对象泛型
     * @return 合并后的对象
     */
    public static <T> T combineFields(Class<T> targetClass, Object... sources) {
        try {
            T target = targetClass.getConstructor().newInstance();
            Arrays.stream(sources).filter(Objects::nonNull).forEach(source -> copyProperties(source, target));
            return target;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("创建目标对象实例失败: " + targetClass.getName(), e);
        }
    }

    /**
     * 将源对象的属性复制到目标对象
     * @param source 源对象
     * @param target 目标对象
     */
    private static void copyProperties(Object source, Object target) {
        try {
            BeanInfo sourceBeanInfo = getBeanInfo(source.getClass());
            BeanInfo targetBeanInfo = getBeanInfo(target.getClass());
            Arrays.stream(sourceBeanInfo.getPropertyDescriptors())
                    .filter(Objects::nonNull)
                    .forEach(sourceDescriptor -> {
                        String propertyName = sourceDescriptor.getName();
                        try {
                            Method readMethod = sourceDescriptor.getReadMethod();
                            if (readMethod == null)
                                return;
                            Object value = readMethod.invoke(source);
                            if (value == null)
                                return;
                            PropertyDescriptor targetDescriptor = findPropertyDescriptor(targetBeanInfo, propertyName);
                            if (targetDescriptor != null) {
                                Method writeMethod = targetDescriptor.getWriteMethod();
                                if (writeMethod != null) {
                                    writeMethod.invoke(target, value);
                                }
                            }
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            log.warn("合并对象字段[{}]失败", propertyName, e);
                        }
                    });
        } catch (IntrospectionException e) {
            throw new RuntimeException("获取Bean信息失败", e);
        }
    }

    /**
     * 获取指定类的BeanInfo，优先从缓存中获取
     * @param clazz 类
     * @return BeanInfo对象
     * @throws IntrospectionException 如果内省失败
     */
    private static BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        return BEAN_INFO_CACHE.computeIfAbsent(clazz, cls -> {
            try {
                return Introspector.getBeanInfo(cls);
            } catch (IntrospectionException e) {
                throw new RuntimeException("获取Bean信息失败: " + cls.getName(), e);
            }
        });
    }

    /**
     * 在BeanInfo中查找指定属性的PropertyDescriptor
     * @param beanInfo     Bean信息
     * @param propertyName 属性名
     * @return PropertyDescriptor对象，如果未找到则返回null
     */
    private static PropertyDescriptor findPropertyDescriptor(BeanInfo beanInfo, String propertyName) {
        return Arrays.stream(beanInfo.getPropertyDescriptors())
                       .filter(pd -> Objects.equals(pd.getName(), propertyName))
                       .findFirst()
                       .orElse(null);
    }

}