package ccxh.top;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@DubboComponentScan(basePackages = "ccxh.top.service.impl")
@MapperScan("ccxh.top.blog.github.mapper")
public class ApplicationAction {
    public static void main(String[] args) {
        //修改为不占用端口启动方法
        new SpringApplicationBuilder(ApplicationAction .class).web(false).run(args);
    }

}
