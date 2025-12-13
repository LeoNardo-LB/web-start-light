package org.smm.archetype.entity.dataobj;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.smm.archetype.entity.api.BaseDO;

import java.time.Instant;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 日志持久化对象
 */
@Getter
@Setter
@Entity
@Table(name = "log")
public class LogDO extends BaseDO {

    private String biz;

    private String method;

    @Column(columnDefinition = "TEXT")
    private String argString;

    @Column(columnDefinition = "TEXT")
    private String resultString;

    private String threadName;

    @Column(columnDefinition = "TEXT")
    private String exception;

    private Long timeCost;

    private Instant startTime;

    private Instant endTime;

}
