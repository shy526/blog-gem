package ccxh.top.service.impl;

import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.config.Service;
import ccxh.top.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author 处理主题目录
 */

@Service
@org.springframework.stereotype.Service
public class ThemeServiceImpl implements ThemeService {
/*    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ThemeMapper themeMapper;*/
    @Override
    public String add(String a, String b) {
        System.out.println("a = " + a);
        return a+b;
    }
}
