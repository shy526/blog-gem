package ccxh.top.api.task;

import ccxh.top.blog.github.mapper.MarkdownPageMapper;
import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.blog.github.mapper.pojo.UserPojo;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class MarkdownAdded implements Runnable {
    private JSONArray added;
    private UserPojo user;
    private GithubUtil githubUtil;
    private ThemeMapper themeMapper;
    private MarkdownPageMapper markdownPageMapper;
    private final static Logger LOGGER=LoggerFactory.getLogger(MarkdownAdded.class);
    public MarkdownAdded(JSONArray added, UserPojo user, GithubUtil githubUtil, ThemeMapper themeMapper, MarkdownPageMapper markdownPageMapper) {
        this.added = added;
        this.user = user;
        this.githubUtil = githubUtil;
        this.themeMapper = themeMapper;
        this.markdownPageMapper = markdownPageMapper;
    }

    @Override
    public void run() {
        Iterator<Object> iterator = added.iterator();
        int themeFlag=0;
        int fileFlag=0;
        while (iterator.hasNext()){
            String path=(String) iterator.next();
            String[] split = path.split("/");
            if(split.length!=2){return;}
            ThemePojo condition = new ThemePojo();
            condition.setUserId(this.user.getId());
            condition.setPath(split[0]);
            ThemePojo themePojo = themeMapper.selectOne(condition);
            if (themePojo==null){
                ThemePojo newTheme = new ThemePojo();
                newTheme.setName(split[0]);
                newTheme.setPath(split[0]);
                newTheme.setUserId(this.user.getId());
                githubUtil.getDesc(newTheme,user);
                newTheme.quickTime();
                themeMapper.insert(newTheme);
                themeFlag++;
                themePojo=newTheme;
            }
            MarkdownPagePojo markdown = githubUtil.createMarkdown(path, this.user);
            markdown.quickTime();
            markdown.setThemeId(themePojo.getId());
            markdownPageMapper.insert(markdown);
            fileFlag++;

        }
        LOGGER.info("新增--- github用户名:{},仓库:{},主题:{},文件:{}",user.getGithubName(),user.getGithubRepot(),themeFlag,fileFlag);
    }
}
