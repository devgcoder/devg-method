package com.github.devgcoder.devgmethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.devgcoder.devgmethod.model.DevgMethodValue;
import com.github.devgcoder.devgmethod.model.HttpStatusEnum;
import com.github.devgcoder.devgmethod.model.ResultModel;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author duheng
 * @Date 2021/4/27 15:03
 */
@Aspect
public class DevgMethodAspect {

	private final Logger logger = LoggerFactory.getLogger(DevgMethodAspect.class);



	@Pointcut("@annotation(com.github.devgcoder.devgmethod.DevgMethod)")
	public void devgMethodPointcut() {
	}

	@Around(value = "devgMethodPointcut()")
	public Object methodPointcut(ProceedingJoinPoint joinPoint) throws Throwable {
		Class clazz = joinPoint.getTarget().getClass();
		String methodName = joinPoint.getSignature().getName();
		Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
		Method method = clazz.getMethod(methodName, parameterTypes);
		DevgMethod devgMethod = method.getAnnotation(DevgMethod.class);
		String name = devgMethod.name();
		String desc = devgMethod.desc();
		if (null == name || name.trim().equals("")) {
			logger.warn("devg-method methodName:{} cannot be null", methodName);
			return joinPoint.proceed();
		}
		JedisPool jedisPool = DevgMethodMemory.redisConfigMap.get(DevgMethodMemory.jedisPool);
		if (null == jedisPool) {
			logger.warn("devg-method jedisPool not init");
			return joinPoint.proceed();
		}
		String nameCachePrefixValue = DevgMethodMemory.nameCachePrefix + name;
		String valueCachePrefixValue = DevgMethodMemory.valueCachePrefix + name;
		String lastStartTime = null;
		String lastEndTime = null;
		long lastDuration = 0L;
		long t1 = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		int expireSeconds = devgMethod.expireSeconds();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Long nx = jedis.setnx(nameCachePrefixValue, "1");
			if (null == nx || nx <= 0) {
				return ResultModel.newFail(HttpStatusEnum.ERROR408.getCode(), HttpStatusEnum.ERROR408.getMessage());
			}
			jedis.expire(nameCachePrefixValue, expireSeconds);
			// 开始时间
			String lastDevgMethodValueString = jedis.get(valueCachePrefixValue);
			if (null != lastDevgMethodValueString && !lastDevgMethodValueString.trim().equals("")) {
				DevgMethodValue lastDevgMethodValue = objectMapper.readValue(lastDevgMethodValueString, DevgMethodValue.class);
				lastStartTime = lastDevgMethodValue.getLastStartTime();
				lastEndTime = lastDevgMethodValue.getLastEndTime();
				lastDuration = lastDevgMethodValue.getLastDuration();
			}
			String theRunningTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			DevgMethodValue devgMethodValue = new DevgMethodValue();
			devgMethodValue.setName(name);
			devgMethodValue.setRunningState((byte)1);
			devgMethodValue.setLastStartTime(lastStartTime);
			devgMethodValue.setLastEndTime(lastEndTime);
			devgMethodValue.setTheRunningTime(theRunningTime);
			devgMethodValue.setLastDuration(lastDuration);
			devgMethodValue.setDesc(desc);
			jedis.set(valueCachePrefixValue, objectMapper.writeValueAsString(devgMethodValue));
			Object object = joinPoint.proceed(); //执行目标方法
			long t2 = System.currentTimeMillis();
			String theEndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			devgMethodValue = new DevgMethodValue();
			devgMethodValue.setName(name);
			devgMethodValue.setRunningState((byte)0);
			devgMethodValue.setLastStartTime(theRunningTime);
			devgMethodValue.setLastEndTime(theEndTime);
			devgMethodValue.setTheRunningTime(null);
			devgMethodValue.setLastDuration(t2 - t1);
			devgMethodValue.setDesc(desc);
			jedis.set(valueCachePrefixValue, objectMapper.writeValueAsString(devgMethodValue));
			return object;
		} catch (Throwable ex) {
			long t2 = System.currentTimeMillis();
			DevgMethodValue devgMethodValue = new DevgMethodValue();
			devgMethodValue.setName(name);
			devgMethodValue.setRunningState((byte)2);
			devgMethodValue.setLastStartTime(lastStartTime);
			devgMethodValue.setLastEndTime(lastEndTime);
			devgMethodValue.setTheRunningTime(null);
			devgMethodValue.setLastDuration(t2 - t1);
			devgMethodValue.setDesc(desc);
			jedis.set(valueCachePrefixValue, objectMapper.writeValueAsString(devgMethodValue));
			logger.error("devg-method error", ex);
			ex.printStackTrace();
			throw new Throwable(ex);
		} finally {
			if (null != jedis) {
				jedis.del(nameCachePrefixValue);
				jedis.close();
			}
		}
	}
}
