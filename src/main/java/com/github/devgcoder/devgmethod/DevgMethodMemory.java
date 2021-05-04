package com.github.devgcoder.devgmethod;

import com.github.devgcoder.devgmethod.model.DevgMethodValue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import redis.clients.jedis.JedisPool;

/**
 * @author duheng
 * @Date 2021/4/26 19:59
 */
public class DevgMethodMemory {

	public static final String nameCachePrefix = "devg:method:name:";
	public static final String valueCachePrefix = "devg:method:value:";
	public static final String jedisPool = "jedisPool";
	public static final Map<String, JedisPool> redisConfigMap = new ConcurrentHashMap<>();
	public static final Map<String, String> beanMethodMap = new ConcurrentHashMap<>();
	public static final Map<String, DevgMethodValue> annotatedMethodMap = new ConcurrentHashMap<>();

}
