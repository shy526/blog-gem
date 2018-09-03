package ccxh.top.schedule;

import ccxh.top.schedule.task.impl.GithubTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 初始化一些做操
 * @author admin
 */
public class ApplicationRun implements ApplicationRunner {
    private final static Logger LOGGER= LoggerFactory.getLogger(ApplicationRun.class);
    @Autowired
    GithubTask githubTask;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        //启动后 查看列表
        githubTask.dispatch();
        LOGGER.info("init project");
    }
}
