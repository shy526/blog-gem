package ccxh.top.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 */
@SpringBootApplication
@MapperScan("ccxh.top.blog.github.mapper")
public class ApplicationAction {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationAction.class, args);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        while (countDownLatch!=null){
            try {
                countDownLatch.await();
                Thread.sleep(10000);
            }catch (Exception e){

            }

        }
    }
}
