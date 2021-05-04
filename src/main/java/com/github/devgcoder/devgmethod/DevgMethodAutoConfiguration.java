package com.github.devgcoder.devgmethod;

import com.github.devgcoder.devgmethod.utils.DevgMethodApplicationContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.github.devgcoder.devgmethod"})
@EnableConfigurationProperties(DevgMethodProperties.class)
@ConditionalOnWebApplication
public class DevgMethodAutoConfiguration {

    private DevgMethodProperties devgMethodProperties;

    public DevgMethodAutoConfiguration(DevgMethodProperties devgMethodProperties) {
        this.devgMethodProperties = devgMethodProperties;
    }


    @Bean
    public DevgMethodComponent devgMethodComponent() {
        DevgMethodComponent devgMethodComponent = new DevgMethodComponent(devgMethodProperties);
        return devgMethodComponent;
    }

    @Bean
    public DevgMethodAspect devgMethodAspect() {
        DevgMethodAspect devgMethodAspect = new DevgMethodAspect();
        return devgMethodAspect;
    }

    @Bean
    public DevgMethodApplicationContextUtil devgMethodApplicationContextUtil() {
        DevgMethodApplicationContextUtil devgMethodApplicationContextUtil = new DevgMethodApplicationContextUtil();
        return devgMethodApplicationContextUtil;
    }

    @Bean
    public DevgMethodBeanPostProcessor devgMethodBeanPostProcessor() {
        DevgMethodBeanPostProcessor devgMethodBeanPostProcessor = new DevgMethodBeanPostProcessor();
        return devgMethodBeanPostProcessor;
    }
}
