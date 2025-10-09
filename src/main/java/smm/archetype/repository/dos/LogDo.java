package smm.archetype.repository.dos;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import smm.archetype.entity.base.BaseEntity;

import java.time.Instant;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 日志持久化对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "log")
public class LogDo extends BaseEntity {
    
    private String biz;
    
    private String method;
    
    private String argString;
    
    private String resultString;
    
    private String threadName;
    
    private String exception;
    
    private Long timeCost;
    
    private Instant startTime;
    
    private Instant endTime;
    
}
