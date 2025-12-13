package org.smm.archetype.controller.global;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.smm.archetype.entity.api.BaseResult;
import org.smm.archetype.entity.api.BizException;
import org.smm.archetype.entity.enums.ResultCode;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 全局异常处理器
 */
@RestControllerAdvice
public class WebExceptionAdvise {
    
    @ExceptionHandler(BizException.class)
    public BaseResult<Void> handleBizException(BizException e) {
        return BaseResult.fail(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(Throwable.class)
    public BaseResult<Void> handleException(Throwable e) {
        return BaseResult.fail(ResultCode.FAIL, e.getMessage());
    }
    
}
