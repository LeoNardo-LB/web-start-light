package org.smm.archetype.service;

import com.alibaba.fastjson2.TypeReference;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 缓存服务
 */
public interface CacheService {
    
    /**
     * 缓存数据
     *
     * @param key   key
     * @param value value
     */
    void put(String key, Object value);
    
    /**
     * 获取缓存数据
     *
     * @param key   key
     * @param clazz 数据类型Class
     * @param <T>   泛型类型
     * @return 数据类型
     */
    <T> T get(String key, Class<T> clazz);
    
    /**
     * 获取缓存数据
     *
     * @param key           key
     * @param typeReference 数据类型引用
     * @param <T>           泛型类型
     * @return 数据类型
     */
    <T> T get(String key, TypeReference<T> typeReference);
    
}
