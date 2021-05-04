package com.github.devgcoder.devgmethod.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.devgcoder.devgmethod.DevgMethodMemory;
import com.github.devgcoder.devgmethod.model.DevgMethodValue;
import com.github.devgcoder.devgmethod.model.HttpStatusEnum;
import com.github.devgcoder.devgmethod.model.ResultModel;
import com.github.devgcoder.devgmethod.utils.DevgMethodApplicationContextUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * @author duheng
 * @Date 2021/4/28 11:06
 */
@RestController
@RequestMapping("/devg/method")
public class DevgMethodController {

	private final Logger logger = LoggerFactory.getLogger(DevgMethodController.class);

	@GetMapping("/invokeMethod")
	public ResultModel invokeMethod(@RequestParam(value = "methodAnnotateddName") String methodAnnotateddName) {
//		String beanName = DevgMethodMemory.annotatedMethodMap.get(methodAnnotateddName);
		DevgMethodValue devgMethodValue = DevgMethodMemory.annotatedMethodMap.get(methodAnnotateddName);
		if (null == devgMethodValue) {
			return ResultModel.newFail("devgMethodValue not exists");
		}
		String beanName = devgMethodValue.getBeanName();
		if (null == beanName || beanName.trim().equals("")) {
			return ResultModel.newFail("beanName not exists");
		}
		Object object = DevgMethodApplicationContextUtil.getBean(beanName);
		if (null == object) {
			return ResultModel.newFail("bean not exists");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				String methodName = DevgMethodMemory.beanMethodMap.get(methodAnnotateddName);
				try {
					Method md = object.getClass().getMethod(methodName, new Class[0]);
					md.invoke(object, new Object[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		return ResultModel.newSuccess();
	}

	@GetMapping("/getValue")
	public ResultModel getValue(@RequestParam(required = false, value = "methodAnnotateddName") String methodAnnoName) {
		Jedis jedis = null;
		try {
			List<DevgMethodValue> results = new ArrayList<>();
			ObjectMapper objectMapper = new ObjectMapper();
			JedisPool jedisPool = DevgMethodMemory.redisConfigMap.get(DevgMethodMemory.jedisPool);
			jedis = jedisPool.getResource();
			if (null == methodAnnoName || methodAnnoName.trim().equals("")) {
				List<Object> objects = new ArrayList<>();
				Set<String> set = jedis.keys(DevgMethodMemory.valueCachePrefix + "*");
				if (null != set && set.size() > 0) {
					Pipeline pipeline = jedis.pipelined();
					for (String k : set) {
						pipeline.get(k);
					}
					objects = pipeline.syncAndReturnAll();
				}
				for (String methodAnnotatedName : DevgMethodMemory.annotatedMethodMap.keySet()) {
					results.add(getDevgMethodValue(DevgMethodMemory.annotatedMethodMap.get(methodAnnotatedName), objects, objectMapper));
				}
			} else {
				DevgMethodValue devgMethodValue = DevgMethodMemory.annotatedMethodMap.get(methodAnnoName);
				if (null == devgMethodValue) {
					return ResultModel.newSuccess(results);
				}
				String desc = devgMethodValue.getDesc();
				String valueCachePrefixValue = DevgMethodMemory.valueCachePrefix + methodAnnoName;
				String jsonStr = jedis.get(valueCachePrefixValue);
				if (null != jsonStr && !jsonStr.equals("")) {
					devgMethodValue = objectMapper.readValue(jsonStr, DevgMethodValue.class);
				}
				devgMethodValue.setDesc(desc);
				results.add(devgMethodValue);
			}
			return ResultModel.newSuccess(results);
		} catch (Exception ex) {
			logger.error("getValue error", ex);
			return ResultModel.newFail(HttpStatusEnum.ERROR500.getCode(), ex.getMessage());
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	private DevgMethodValue getDevgMethodValue(DevgMethodValue devgMethodValue, List<Object> objects, ObjectMapper objectMapper) throws Exception {
//		DevgMethodValue devgMethodValue = new DevgMethodValue();
//		devgMethodValue.setName(methodAnnotatedName);
		String methodAnnotatedName = devgMethodValue.getName();
		String desc = devgMethodValue.getDesc();
		if (null == objects || objects.size() <= 0) {
			return devgMethodValue;
		}
		for (Object object : objects) {
//			String jsonStr = objectMapper.writeValueAsString(object);
			devgMethodValue = objectMapper.readValue(object.toString(), DevgMethodValue.class);
			String name = devgMethodValue.getName();
			if (null != name && name.equals(methodAnnotatedName)) {
				devgMethodValue.setDesc(desc);
				return devgMethodValue;
			}
		}
		return devgMethodValue;

	}
}
