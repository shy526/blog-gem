package ccxh.top.config;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@org.springframework.stereotype.Service
@com.alibaba.dubbo.config.annotation.Service(version = "1.0.0")
/**
 * 自定义dubbo注解 同时注册两个
 */
public @interface Service {
}
