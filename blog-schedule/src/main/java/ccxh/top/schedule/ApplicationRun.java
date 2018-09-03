package ccxh.top.schedule;

import ccxh.top.schedule.task.impl.GithubTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 初始化一些做操
 * @author admin
 */
public class ApplicationRun implements ApplicationRunner {
    @Autowired
    GithubTask githubTask;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        //启动后 查看列表
        githubTask.dispatch();
    }
}
