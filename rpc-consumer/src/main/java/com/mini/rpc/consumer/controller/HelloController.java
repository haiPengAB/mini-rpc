package com.mini.rpc.consumer.controller;

import com.mini.rpc.consumer.annotation.RpcReference;
import com.mini.rpc.provider.facade.HelloFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    /**
     * @RpcReference 注解通常与框架的代理机制结合使用。当 Spring 容器初始化这个 Bean 时，
     * 会自动生成一个代理对象，并将该代理对象注入到标注了 @RpcReference 的字段中。
     * 这个代理对象负责处理远程服务调用的细节，包括序列化、网络传输和反序列化等。
     */
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
    private HelloFacade helloFacade;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String sayHello() {
        return helloFacade.hello("mini rpc");
    }
}
