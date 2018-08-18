package ccxh.top.blog.github.mapper;


import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.runner.RunWith;

import org.junit.Test;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationAction.class)
public class ThemeMapperTest {
    @Autowired
    ThemeMapper themeMapper;
    @Test
    public void test1(){
        ThemePojo themePojo = new ThemePojo();
        themePojo.quickTime();
        Integer insert = themeMapper.insert(themePojo);
        System.out.println("insert = " + insert);

    }
}