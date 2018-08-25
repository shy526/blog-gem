package ccxh.top.schedule;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("ccxh.top.blog.github.mapper")
public class ApplicationAction  {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationAction.class, args);
    }



}
