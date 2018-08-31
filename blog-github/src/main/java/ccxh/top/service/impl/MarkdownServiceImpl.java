package ccxh.top.service.impl;

import ccxh.top.blog.github.mapper.MarkdownPageMapper;
import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.config.Service;
import ccxh.top.exception.Assert;
import ccxh.top.pojo.Result;
import ccxh.top.service.MarkdownService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 负责markdwn
 * @author admin
 */
@Service
public class MarkdownServiceImpl implements MarkdownService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MarkdownPageMapper markdownPageMapper;
    @Override
    public Result getMarkdownPageBy(Integer themeId){
        Assert.isNull(themeId,"themeId not null");
        MarkdownPagePojo condition=new MarkdownPagePojo();
        condition.setThemeId(themeId);
        List<MarkdownPagePojo> select = markdownPageMapper.select(condition);
        return Result.ok(select);
    }
}
