package smm.archetype.entity.base;

import lombok.Data;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 基础请求
 */
@Data
public class BaseRequest {
    
    /**
     * 请求序列号
     */
    private String requestId;
    
    /**
     * traceId
     */
    private String traceId;
    
}
