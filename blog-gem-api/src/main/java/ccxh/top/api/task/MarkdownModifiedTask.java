package ccxh.top.api.task;

import ccxh.top.blog.github.mapper.MarkdownPageMapper;
import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.blog.github.mapper.pojo.UserPojo;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;

public class MarkdownModifiedTask implements Runnable {
    private JSONArray added;
    private UserPojo user;
    private String markdownRootPath;
    private GithubUtil githubUtil;
    private MarkdownPageMapper markdownPageMapper;
    private ThemeMapper themeMapper;
    private final  static Logger LOGGER=LoggerFactory.getLogger(MarkdownModifiedTask.class);

    public MarkdownModifiedTask(JSONArray added, UserPojo user, String markdownRootPath, GithubUtil githubUtil, MarkdownPageMapper markdownPageMapper, ThemeMapper themeMapper) {
        this.added = added;
        this.user = user;
        this.markdownRootPath = markdownRootPath;
        this.githubUtil = githubUtil;
        this.markdownPageMapper = markdownPageMapper;
        this.themeMapper = themeMapper;
    }

    @Override
    public void run() {
        Iterator<Object> iterator = added.iterator();
        while (iterator.hasNext()){

            String path = (String) iterator.next();
            if (path.indexOf(GithubUtil.README)!=-1){
                //说明新增了说明
                ThemePojo themePojo = githubUtil.getParentTheme(path,this.user);
                githubUtil.getDesc(themePojo,this.user);
                ThemePojo desc = new ThemePojo();
                desc.setId(themePojo.getId());
                desc.setDes(themePojo.getDes());
                themeMapper.updateByPrimaryKeySelective(desc);
                continue;
            }
            File file = Paths.get(markdownRootPath, user.getGithubName(), user.getGithubRepot(), path+".html").toFile();
            if (file.exists()){
                if (file.isFile()){
                    ThemePojo parent = githubUtil.getParentTheme(path,this.user);
                    MarkdownPagePojo condition = new MarkdownPagePojo();
                    condition.setPath(path);
                    condition.setThemeId(parent.getId());
                    MarkdownPagePojo pagePojo = markdownPageMapper.selectOne(condition);
                    markdownPageMapper.updateTimeById(pagePojo.getId(),System.currentTimeMillis());
                    githubUtil.createMarkdown(path,this.user);
                }
            }
        }
        LOGGER.info("修改--- github用户名:{},仓库:{},文件:{}",user.getGithubName(),user.getGithubRepot(),added.size());
    }


}
