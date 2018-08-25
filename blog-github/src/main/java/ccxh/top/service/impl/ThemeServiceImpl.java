package ccxh.top.service.impl;

import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.config.Service;
import ccxh.top.eception.ServiceException;
import ccxh.top.pojo.Result;
import ccxh.top.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 *
 * @author 处理主题目录
 */

@Service
@org.springframework.stereotype.Service
public class ThemeServiceImpl implements ThemeService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ThemeMapper themeMapper;
    @Override
    public Result thenmePage(Integer pageIndex, Integer num) {
        if (pageIndex==null||pageIndex<=0){
            throw new ServiceException("pageInde gt 0");
        }
        int backPageNum = (pageIndex - 1) * num;
        return Result.ok(themeMapper.selectThemePage(backPageNum,num));
    }
}
