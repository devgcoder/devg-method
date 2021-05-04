package com.github.devgcoder.devgmethod.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author duheng
 * @Date 2021/1/28 19:16
 */
public class DevgMethodApplicationContextUtil implements ApplicationContextAware {

  private static ApplicationContext applicationContext;


  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    DevgMethodApplicationContextUtil.applicationContext = applicationContext;
  }
  /**
   * @return ApplicationContext
   */
  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * 获取对象
   *
   * @param name spring配置文件中配置的bean名或注解的名称
   * @return 一个以所给名字注册的bean的实例
   * @throws BeansException 抛出spring异常
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) throws BeansException {
    return (T) applicationContext.getBean(name);
  }

  /**
   * 获取类型为requiredType的对象
   *
   * @param clazz 需要获取的bean的类型
   * @return 该类型的一个在ioc容器中的bean
   * @throws BeansException 抛出spring异常
   */
  public static <T> T getBean(Class<T> clazz) throws BeansException {
    return applicationContext.getBean(clazz);
  }

  /**
   * 如果ioc容器中包含一个与所给名称匹配的bean定义，则返回true否则返回false
   *
   * @param name ioc容器中注册的bean名称
   * @return 存在返回true否则返回false
   */
  public static boolean containsBean(String name) {
    return applicationContext.containsBean(name);
  }

}
