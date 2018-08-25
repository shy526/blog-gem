package ccxh.top;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@DubboComponentScan(basePackages = "ccxh.top.service.impl")
@MapperScan("ccxh.top.blog.github.mapper")
public class ApplicationAction implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationAction.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
/*        while (countDownLatch!=null){
            countDownLatch.await();
            System.out.println("await");
        }*/
    }
}
