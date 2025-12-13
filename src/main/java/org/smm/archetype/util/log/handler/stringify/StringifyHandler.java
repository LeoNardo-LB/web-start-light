package org.smm.archetype.util.log.handler.stringify;

/**
 * 对象序列化处理器
 */
public interface StringifyHandler {
    
    /**
     * 获取字符串化类型
     *
     * @return 字符串化类型
     */
    StringifyType getStringifyType();
    
    /**
     * 转字符串
     *
     * @param target 对象
     * @return 字符串
     */
    String stringify(Object target);
    
}
