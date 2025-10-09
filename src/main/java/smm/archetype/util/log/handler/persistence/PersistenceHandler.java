package smm.archetype.util.log.handler.persistence;

import smm.archetype.util.log.BizLogDto;

/**
 * @author Leonardo
 * @since 2025/7/15
 * 持久化接口
 */
public interface PersistenceHandler {
    
    /**
     * 持久化类型
     */
    PersistenceType getPersistenceType();
    
    /**
     * 持久化
     */
    void persist(BizLogDto BizLogDto);
    
}
