package smm.archetype.controller.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import smm.archetype.entity.api.BasePageRequest;
import smm.archetype.entity.api.BasePageResult;
import smm.archetype.entity.dataobj.LogDO;
import smm.archetype.repository.LogRepository;
import smm.archetype.util.log.BizLog;
import smm.archetype.util.log.handler.persistence.PersistenceType;

import java.util.List;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 测试Controller
 */
@Slf4j
@RestController
public class TestController {
    
    private final LogRepository logRepository;
    
    public TestController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }
    
    @GetMapping("hello")
    @BizLog(value = "测试hello", persistence = PersistenceType.DB)
    List<List<String>> hello(KVRequest request) {
        if (request == null) {
            return List.of(List.of("hello", "world"), List.of("java", "spring", "mybatis", "hibernate"));
        }
        log.info(request.toString());
        return List.of(List.of(request.key(), request.value()));
    }
    
    @GetMapping("exception")
    @BizLog(value = "测试异常", persistence = PersistenceType.DB)
    List<List<String>> exception() {
        throw new RuntimeException("test");
    }
    
    @GetMapping("logs")
    public BasePageResult<LogDO> logs(BasePageRequest request) {
        Page<LogDO> all = logRepository.findAll(request.toJpaType());
        return BasePageResult.fromPage(all);
    }
    
    /**
     * 测试的kv请求类
     */
    private record KVRequest(String key, String value) {}
    
}
