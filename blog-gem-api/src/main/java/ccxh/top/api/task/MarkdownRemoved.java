package ccxh.top.api.task;

import ccxh.top.api.util.IOUtil;
import ccxh.top.blog.github.mapper.MarkdownPageMapper;
import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.blog.github.mapper.pojo.UserPojo;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class MarkdownRemoved implements Runnable {
    private JSONArray delete;
    private UserPojo user;
    private String markdownRootPath;
    private MarkdownPageMapper markdownPageMapper;
    private ThemeMapper themeMapper;
    private final  static Logger LOGGER=LoggerFactory.getLogger(MarkdownRemoved.class);
    public MarkdownRemoved(JSONArray delete, UserPojo user, String markdownRootPath, MarkdownPageMapper markdownPageMapper, ThemeMapper themeMapper) {
        this.delete = delete;
        this.user = user;
        this.markdownRootPath = markdownRootPath;
        this.markdownPageMapper = markdownPageMapper;
        this.themeMapper = themeMapper;
    }

    @Override
    public void run() {
        Iterator<Object> iterator = delete.iterator();
        int themeFlag=0,fileFlag=0;
        while (iterator.hasNext()){
            String path=(String) iterator.next();
            Path fixation = Paths.get(this.markdownRootPath, user.getGithubName(), user.getGithubRepot(), path+".html");
            File file = fixation.toFile();
            if (!file.exists()) {
                return;
            }
            if (file.isFile()){
                file.delete();
                ThemePojo condition = new ThemePojo();
                condition.setPath(path.split("/")[0]);
                condition.setUserId(this.user.getId());
                ThemePojo themePojo = themeMapper.selectOne(condition);
                MarkdownPagePojo pagePojo = new MarkdownPagePojo();
                pagePojo.setThemeId(themePojo.getId());
                pagePojo.setPath(path);
                markdownPageMapper.delete(pagePojo);
                fileFlag++;
                File[] files =fixation.getParent().toFile().listFiles() ;
                if (files==null||files.length<=0){
                    //没有子文件时 说明 主题被删除
                    IOUtil.deleteDir(fixation.getParent().toString());
                    themeMapper.deleteByPrimaryKey(themePojo);
                    themeFlag++;
                }
            }
        }
        LOGGER.info("删除--- github用户名:{},仓库:{},主题:{},文件:{}",user.getGithubName(),user.getGithubRepot(),themeFlag,fileFlag);

    }
}
