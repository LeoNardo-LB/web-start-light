package org.smm.archetype.entity.enums;

import lombok.Getter;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 结果码
 */
@Getter
public enum ResultCode {
    
    /**
     * 超过
     */
    SUCCESS(1000, "成功"),
    
    /**
     * 失败
     */
    FAIL(2000, "失败"),
    ILLEGAL_ARGUMENT(2001, "参数有误"),
    RPC_EXCEPTION(2002, "远程调用失败"),
    
    ;
    
    /**
     * code
     */
    private final int code;
    
    /**
     * 默认信息
     */
    private final String message;
    
    /**
     * 构造器
     */
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
}
