package org.smm.archetype.entity.api;

import lombok.Data;
import org.smm.archetype.entity.enums.ResultCode;

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
     * 是否成功
     */
    private boolean success;

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
        BaseResult<Void> result = new BaseResult<>();
        result.code = code.getCode();
        result.success = false;
        result.message = msg;
        return result;
    }

    public static <T> BaseResult<T> success(T data) {
        BaseResult<T> result = new BaseResult<>();
        result.code = ResultCode.SUCCESS.getCode();
        result.message = ResultCode.SUCCESS.getMessage();
        result.success = true;
        result.data = data;
        return result;
    }

}
