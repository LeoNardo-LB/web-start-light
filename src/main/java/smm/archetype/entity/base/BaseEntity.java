package smm.archetype.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import smm.archetype.util.UserContext;

import java.time.Instant;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 基础实体
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createTime;
    
    @LastModifiedDate
    @Column(nullable = false)
    private Instant updateTime;
    
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createUser;
    
    @LastModifiedBy
    @Column(nullable = false)
    private String updateUser;
    
    @Version
    private Integer version;
    
    /**
     * 设置创建动作
     */
    @PrePersist
    public void onCreate() {
        createTime = Instant.now();
        updateTime = Instant.now();
        createUser = UserContext.getId();
        updateUser = UserContext.getId();
    }
    
    /**
     * 设置更新动作
     */
    @PreUpdate
    public void onUpdate() {
        updateTime = Instant.now();
        updateUser = UserContext.getId();
    }
    
}
