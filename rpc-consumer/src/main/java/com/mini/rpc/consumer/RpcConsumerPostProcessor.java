package com.mini.rpc.consumer;

import com.mini.rpc.common.RpcConstants;
import com.mini.rpc.consumer.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * BeanFactoryPostProcessor 是 Spring 容器加载 Bean 的定义之后以及 Bean 实例化之前执行，
 * 所以 BeanFactoryPostProcessor 可以在 Bean 实例化之前获取 Bean 的配置元数据，并允许用户对其修改。
 * 而 BeanPostProcessor 是在 Bean 初始化前后执行，它并不能修改 Bean 的配置信息。
 */
@Component
@Slf4j
public class RpcConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {

    private ApplicationContext context;

    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> rpcRefBeanDefinitions = new LinkedHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    /**
     * 查找被 @RpcReference 注解标注的字段，为这些字段创建相应的 Bean 定义，并将其注册到 Spring 容器中。这样，在应用程序运行时，
     * 带有 @RpcReference 注解的字段将被自动注入相应的代理对象，从而实现对远程服务的调用。
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 遍历所有 Bean 的定义
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.classLoader); //使用类加载器解析类名并加载类。
                // 遍历类中的所有字段，调用 parseRpcReference 方法处理每个字段。这一步的目的是查找和解析被 @RpcReference 注解标注的字段。
                ReflectionUtils.doWithFields(clazz, this::parseRpcReference);
            }
        }
        // 将 beanFactory 转换为 BeanDefinitionRegistry，以便注册新的 Bean 定义
        // BeanDefinitionRegistry 是 Spring 框架中的一个接口，它用于在 Spring 容器中注册和管理 Bean 定义（BeanDefinition）。
        // 通过这个接口，开发者可以以编程方式向 Spring 容器添加、移除或查询 Bean 定义，而不是仅通过 XML 配置文件或注解的方式。
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        this.rpcRefBeanDefinitions.forEach((beanName, beanDefinition) -> {
            if (context.containsBean(beanName)) {
                throw new IllegalArgumentException("spring context already has a bean named " + beanName);
            }
            registry.registerBeanDefinition(beanName, rpcRefBeanDefinitions.get(beanName));
            log.info("registered RpcReferenceBean {} success.", beanName);
        });
    }


    //为 RpcReferenceBean 的成员变量赋值，包括服务类型 interfaceClass、服务版本 serviceVersion、
    // 注册中心类型 registryType、注册中心地址 registryAddr 以及超时时间 timeout。
    private void parseRpcReference(Field field) {
        // 拿到RpcReference注解的对象
        RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);

        /**
         *  parseRpcReference 方法用于处理字段上的 @RpcReference 注解，并将解析后的 Bean 定义添加到 rpcRefBeanDefinitions 中。
         */
        if (annotation != null) {
            // BeanDefinitionBuilder 是 Spring 框架的一部分，它用于以编程的方式构建 BeanDefinition 对象。
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            // 为通过 BeanDefinitionBuilder 创建的 BeanDefinition 设置一个初始化方法。在 Spring 容器中，
            // 当一个 Bean 被创建并实例化之后，Spring 会调用指定的初始化方法来进行一些额外的初始化操作。
            builder.setInitMethodName(RpcConstants.INIT_METHOD_NAME);
            builder.addPropertyValue("interfaceClass", field.getType());
            builder.addPropertyValue("serviceVersion", annotation.serviceVersion());
            builder.addPropertyValue("registryType", annotation.registryType());
            builder.addPropertyValue("registryAddr", annotation.registryAddress());
            builder.addPropertyValue("timeout", annotation.timeout());

            BeanDefinition beanDefinition = builder.getBeanDefinition();
            rpcRefBeanDefinitions.put(field.getName(), beanDefinition);
        }
    }

}
