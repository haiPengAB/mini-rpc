package com.mini.rpc.consumer;

import com.mini.rpc.provider.registry.RegistryFactory;
import com.mini.rpc.provider.registry.RegistryService;
import com.mini.rpc.provider.registry.RegistryType;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;


/**
 * Spring 的 FactoryBean 接口可以帮助我们实现自定义的 Bean，
 * FactoryBean 是一种特种的工厂 Bean，通过 getObject() 方法返回对象，而并不是 FactoryBean 本身。
 *
 *  @descriube #(BeanFactory和FactoryBean的接口)
 *  BeanFactory是SpringloC容器的一个接口，用来获取Bean以及管理Bean的依赖注入和生命周期。
 *  FactoryBean是一个接口，用于定义一个工厂Bean，它可以产生某种类型的对象。当在Spring配置文件中定义一个Bean时，
 *  如果这个Bean实现了FactoryBean接口，那么Spring容器不直接返回这个Bean实例，而是返回FactoryBean#getObject()方法所返回的对象。
 */
public class RpcReferenceBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;

    private String serviceVersion;

    private String registryType;

    private String registryAddr;

    private long timeout;

    private Object object;

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void init() throws Exception {
        // 生成动态代理对象并赋值给 object
        RegistryService registryService = RegistryFactory.getInstance(this.registryAddr, RegistryType.valueOf(this.registryType));
        this.object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcInvokerProxy(serviceVersion, timeout, registryService));
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
