package ccxh.top.service.impl;


import ccxh.top.ApplicationAction;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.pojo.Result;
import ccxh.top.service.ThemeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationAction.class)
public class ThemeServiceImplTest {
    @Autowired
    ThemeService themeService;
    @Test
    public void thenmePage() {
        Result result = themeService.thenmePage(-1, 1);
        System.out.println("list = " );
    }
}