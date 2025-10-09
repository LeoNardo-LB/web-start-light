package smm.archetype.entity.base;

import lombok.Data;
import smm.archetype.entity.enums.ResultCode;

/**
 * @param <T> 结果类型
 * @author Leonardo
 * @since 2025/7/14
 * 基础结果
 */
@Data
public class BaseResult<T> {
    
    /**
     * code
     */
    private int code;
    
    /**
     * 信息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * traceId
     */
    private String traceId;
    
    /**
     * requestId
     */
    private String requestId;
    
    public static BaseResult<Void> fail() {
        return fail(ResultCode.FAIL);
    }
    
    public static BaseResult<Void> fail(ResultCode code) {
        return fail(code, code.getMessage());
    }
    
    public static BaseResult<Void> fail(ResultCode code, String msg) {
        BaseResult<Void> BaseResult = new BaseResult<>();
        BaseResult.code = code.getCode();
        BaseResult.message = msg;
        return BaseResult;
    }
    
    public BaseResult<T> success(T data) {
        this.code = ResultCode.SUCCESS.getCode();
        this.message = ResultCode.SUCCESS.getMessage();
        this.data = data;
        return this;
    }
    
}
