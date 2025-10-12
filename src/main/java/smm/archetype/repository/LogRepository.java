package smm.archetype.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Leonardo
 * @since 2025/7/14
 * log 仓储
 */
@Repository
public interface LogRepository extends JpaRepository<LogDo, Long> {

}
