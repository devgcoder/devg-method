package com.github.devgcoder.devgmethod;

import com.github.devgcoder.devgmethod.model.DevgMethodValue;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author duheng
 * @Date 2021/4/30 16:41
 */
public class DevgMethodBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class targetClass = bean.getClass();
//		Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
//				(MethodIntrospector.MetadataLookup<Set<Scheduled>>) method -> {
//					Set<Scheduled> scheduledMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(
//							method, Scheduled.class, Schedules.class);
//					return (!scheduledMethods.isEmpty() ? scheduledMethods : null);
//				});
		Map<Method, DevgMethod> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
				new MethodIntrospector.MetadataLookup<DevgMethod>() {
					@Override
					public DevgMethod inspect(Method method) {
						return AnnotatedElementUtils.findMergedAnnotation(method, DevgMethod.class);
					}
				});
		if (null == annotatedMethods || annotatedMethods.isEmpty()) {
      return bean;
		}
		for (Method method : annotatedMethods.keySet()) {
			DevgMethod devgMethod = method.getAnnotation(DevgMethod.class);
			String methodAnnotatedName = devgMethod.name();
			String desc = devgMethod.desc();
			if (DevgMethodMemory.beanMethodMap.containsKey(methodAnnotatedName)) {
				throw new RuntimeException("the devg method name" + methodAnnotatedName + " exists");
			}
			DevgMethodValue devgMethodValue = new DevgMethodValue();
			devgMethodValue.setName(methodAnnotatedName);
			devgMethodValue.setDesc(desc);
			devgMethodValue.setBeanName(beanName);
			String methodName = method.getName();
//				String beanMethodKey = targetClass.getName() + ClazzUtil.PACKAGE_SEPARATOR + methodAnnotateddName;
			DevgMethodMemory.beanMethodMap.put(methodAnnotatedName, methodName);
			DevgMethodMemory.annotatedMethodMap.put(methodAnnotatedName, devgMethodValue);
		}
		return bean;
	}
}
