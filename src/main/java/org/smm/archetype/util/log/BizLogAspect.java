package org.smm.archetype.util.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.smm.archetype.util.log.handler.persistence.PersistenceHandler;
import org.smm.archetype.util.log.handler.persistence.PersistenceType;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Leonardo
 * @since 2025/7/15
 * 业务日志切面
 */
@Slf4j
@Aspect
@Order
@Component
public class BizLogAspect {

    private final Map<PersistenceType, PersistenceHandler> handlerMap;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public BizLogAspect(List<PersistenceHandler> persistenceHandlers) {

        this.handlerMap = persistenceHandlers.stream().collect(Collectors.toMap(PersistenceHandler::getPersistenceType, s -> s));
    }

    @Pointcut("@annotation(org.smm.archetype.util.log.BizLog)")
    public void bizLogCut() {
    }

    @Around(value = "bizLogCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        BizLog bizLog = signature.getMethod().getAnnotation(BizLog.class);
        // 构建日志信息
        BizLogDto BizLogDto = new BizLogDto();
        BizLogDto.setArgs(joinPoint.getArgs());
        BizLogDto.setSignature(signature);
        BizLogDto.setBizLog(bizLog);
        BizLogDto.setThreadName(Thread.currentThread().getName());
        try {
            // 执行目标方法
            BizLogDto.setStartTime(Instant.now());
            Object result = joinPoint.proceed();
            BizLogDto.setResult(result);
            return result;
        } catch (Throwable e) {
            // 捕获异常, 记录异常信息
            BizLogDto.setError(e);
            throw e;
        } finally {
            // 持久化
            BizLogDto.setEndTime(Instant.now());
            executorService.execute(() -> {
                PersistenceType[] persistence = bizLog.persistence();
                for (PersistenceType one : persistence) {
                    Optional.ofNullable(handlerMap.get(one)).ifPresent(handler -> handler.persist(BizLogDto));
                }
            });
        }
    }

}
