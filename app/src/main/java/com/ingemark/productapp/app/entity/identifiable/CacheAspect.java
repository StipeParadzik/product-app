package com.ingemark.productapp.app.entity.identifiable;

import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
@Order(0)
public class CacheAspect
{
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "entity:";
    private static final String PACKAGE_PREFIX = "com.ingemark.productapp.app.customer";
    private static final Duration DEFAULT_DURATION = Duration.ofHours(1);

    public CacheAspect(RedisTemplate<String, Object> redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("execution(* com.ingemark.productapp.app.customer..*Repository.findById(..))")
    public void cacheableFindById()
    {
    }

    @Pointcut("execution(* com.ingemark.productapp.app.customer..*Repository.findAll(..))")
    public void cacheableFindAll()
    {
    }

    @Pointcut("execution(* com.ingemark.productapp.app.customer..*Repository.save(..))")
    public void cacheEvictOnSave()
    {
    }

    @Pointcut("execution(* com.ingemark.productapp.app.customer..*Repository.deleteById(..))")
    public void cacheEvictOnDelete()
    {
    }

    @Around("cacheableFindById()")
    public Object cacheFindById(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {

        Object target = proceedingJoinPoint.getTarget();
        String entityName = extractEntityName(target);
        Object[] args = proceedingJoinPoint.getArgs();
        if (entityName == null || args == null || args.length == 0)
        {
            return proceedingJoinPoint.proceed();
        }

        Integer id = (Integer) args[0];

        String cacheKey = getCacheKeyById(entityName, id);

        Object cachedEntity = redisTemplate.opsForValue()
            .get(cacheKey);
        if (cachedEntity != null)
        {
            return Optional.of(cachedEntity);
        }

        Object result = proceedingJoinPoint.proceed();
        if (result instanceof Optional<?> optional && optional.isPresent())
        {
            redisTemplate.opsForValue()
                .set(cacheKey, optional.get(), DEFAULT_DURATION);
        }

        return result;
    }

    @Around("cacheableFindAll()")
    public Object cacheFindAll(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {
        Object target = proceedingJoinPoint.getTarget();
        String entityName = extractEntityName(target);

        if (entityName == null)
        {
            return proceedingJoinPoint.proceed();
        }

        String cacheKey = getCacheKeyForAll(entityName);

        Object cachedEntities = redisTemplate.opsForValue()
            .get(cacheKey);
        if (cachedEntities != null)
        {
            return cachedEntities;
        }

        Object result = proceedingJoinPoint.proceed();
        redisTemplate.opsForValue()
            .set(cacheKey, result, DEFAULT_DURATION);

        return result;
    }

    @Around("cacheEvictOnSave()")
    public Object evictOnSave(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {
        Object result = proceedingJoinPoint.proceed();

        if (result instanceof IdentifiableEntity entity)
        {
            String entityName = entity.getClass()
                .getSimpleName();
            evictCache(entity.getId(), entityName);
            redisTemplate.opsForValue()
                .set(getCacheKeyById(entityName, entity.getId()), result, DEFAULT_DURATION);
        }

        return result;
    }

    @Around("cacheEvictOnDelete()")
    public Object evictOnDelete(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {
        Object[] args = proceedingJoinPoint.getArgs();

        if (args == null || args.length == 0)
        {
            return proceedingJoinPoint.proceed();
        }
        Integer id = (Integer) args[0];
        Object target = proceedingJoinPoint.getTarget();
        String entityName = extractEntityName(target);

        proceedingJoinPoint.proceed();
        evictCache(id, entityName);

        return null;
    }

    private String getCacheKeyById(String entityName, Object id)
    {
        return CACHE_KEY_PREFIX + entityName + ":" + id;
    }

    private String getCacheKeyForAll(String entityName)
    {
        return CACHE_KEY_PREFIX + "all:" + entityName;
    }

    private void evictCache(Integer id, String entityName)
    {
        String cacheKeyById = getCacheKeyById(entityName, id);
        String cacheKeyAll = getCacheKeyForAll(entityName);

        redisTemplate.delete(cacheKeyById);
        redisTemplate.delete(cacheKeyAll);
    }

    private String extractEntityName(Object target)
    {
        Class<?> targetClass = target.getClass();
        return Arrays.stream(targetClass.getInterfaces())
            .filter(iface -> iface.getPackageName()
                .startsWith(PACKAGE_PREFIX))
            .flatMap(iface -> Arrays.stream(iface.getGenericInterfaces()))
            .filter(ParameterizedType.class::isInstance)
            .map(type -> ((ParameterizedType) type).getActualTypeArguments()[0])
            .filter(entityType -> entityType instanceof Class<?>)
            .map(entityType -> ((Class<?>) entityType).getSimpleName())
            .findFirst()
            .orElse(null);
    }
}

