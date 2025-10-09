package smm.archetype.util.log.handler.persistence;

import org.springframework.stereotype.Component;
import smm.archetype.repository.LogRepository;
import smm.archetype.repository.dos.LogDo;
import smm.archetype.util.log.BizLog;
import smm.archetype.util.log.BizLogDto;
import smm.archetype.util.log.handler.stringify.JdkStringifyHandler;
import smm.archetype.util.log.handler.stringify.StringifyHandler;
import smm.archetype.util.log.handler.stringify.StringifyType;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Leonardo
 * @since 2025/7/15
 * 数据库持久化
 */
@Component
public class DbPersistenceHandler implements PersistenceHandler {
    
    private final LogRepository logRepository;
    
    private final Map<StringifyType, StringifyHandler> stringifyHandlerMap;
    
    public DbPersistenceHandler(LogRepository logRepository, List<StringifyHandler> stringifyHandlers) {
        this.stringifyHandlerMap = stringifyHandlers.stream().collect(Collectors.toMap(StringifyHandler::getStringifyType, h -> h));
        this.logRepository = logRepository;
    }
    
    @Override
    public PersistenceType getPersistenceType() {
        return PersistenceType.DB;
    }
    
    @Override
    public void persist(BizLogDto BizLogDto) {
        // 选择handler
        BizLog bizLog = BizLogDto.getBizLog();
        StringifyType stringify = bizLog.stringify();
        StringifyHandler handler = Optional.ofNullable(stringifyHandlerMap.get(stringify)).orElse(new JdkStringifyHandler());
        // 构建日志并保存
        LogDo logDo = new LogDo();
        logDo.setBiz(bizLog.value());
        logDo.setMethod(BizLogDto.getSignature().toLongString());
        logDo.setArgString(handler.stringify(BizLogDto.getArgs()));
        logDo.setResultString(handler.stringify(BizLogDto.getResult()));
        logDo.setThreadName(BizLogDto.getThreadName());
        Optional.ofNullable(BizLogDto.getError()).ifPresent(e -> logDo.setException(e.toString()));
        logDo.setTimeCost(Duration.between(BizLogDto.getStartTime(), BizLogDto.getEndTime()).toMillis());
        logDo.setStartTime(BizLogDto.getStartTime());
        logDo.setEndTime(BizLogDto.getEndTime());
        logRepository.save(logDo);
    }
    
}
