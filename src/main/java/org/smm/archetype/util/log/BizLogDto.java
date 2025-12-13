package org.smm.archetype.util.log;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.reflect.MethodSignature;

import java.time.Instant;

/**
 * @author Leonardo
 * @since 2025/7/15
 * 业务日志DTO
 */
@Getter
@Setter
public class BizLogDto {
    
    private BizLog bizLog;
    
    private MethodSignature signature;
    
    private Object[] args;
    
    private Object result;
    
    private String threadName;
    
    private Throwable error;
    
    private Instant startTime;
    
    private Instant endTime;
    
}
