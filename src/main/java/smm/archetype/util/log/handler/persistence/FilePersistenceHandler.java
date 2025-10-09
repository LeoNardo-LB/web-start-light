package smm.archetype.util.log.handler.persistence;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
 * 文件持久化
 */
@Component
public class FilePersistenceHandler implements PersistenceHandler {
    
    private final Map<StringifyType, StringifyHandler> stringifyHandlerMap;
    
    private final JdkStringifyHandler JDKStringifyHandler;
    
    public FilePersistenceHandler(List<StringifyHandler> stringifyHandlers, JdkStringifyHandler JDKStringifyHandler) {
        this.stringifyHandlerMap = stringifyHandlers.stream().collect(Collectors.toMap(StringifyHandler::getStringifyType, h -> h));
        this.JDKStringifyHandler = JDKStringifyHandler;
    }
    
    @Override
    public PersistenceType getPersistenceType() {
        return PersistenceType.FILE;
    }
    
    @Override
    public void persist(BizLogDto BizLogDto) {
        // 输出实际的类
        Class<?> declaringClass = BizLogDto.getSignature().getMethod().getDeclaringClass();
        Logger logger = LoggerFactory.getLogger(declaringClass);
        
        BizLog bizLog = BizLogDto.getBizLog();
        StringifyType stringify = bizLog.stringify();
        MethodSignature signature = BizLogDto.getSignature();
        Object[] args = BizLogDto.getArgs();
        Object result = BizLogDto.getResult();
        Throwable error = BizLogDto.getError();
        
        StringifyHandler handler = Optional.ofNullable(stringifyHandlerMap.get(stringify)).orElse(JDKStringifyHandler);
        StringBuilder builder = new StringBuilder();
        // 业务名称
        if (StringUtils.isNotBlank(bizLog.value())) {
            builder.append("Biz:[").append(bizLog.value()).append("]; ");
        }
        // 方法
        builder.append("Method:[").append(signature.toShortString()).append("]; ");
        // 耗时
        builder.append("Cost:[").append(Duration.between(BizLogDto.getStartTime(), BizLogDto.getEndTime()).toMillis()).append("ms]; ");
        // 线程名称
        builder.append("Thread:[").append(BizLogDto.getThreadName()).append("]; ");
        // 入参
        if (args != null && args.length > 0) {
            builder.append("Args:[").append(handler.stringify(args)).append("]; ");
        }
        // 返回值
        if (result != null) {
            builder.append("BaseResult:[").append(handler.stringify(result)).append("]; ");
        }
        if (error == null) {
            logger.info(builder.toString());
        } else {
            builder.append("Exception:[").append(error.getMessage()).append("]; ");
            logger.error(builder.toString());
        }
    }
    
}
