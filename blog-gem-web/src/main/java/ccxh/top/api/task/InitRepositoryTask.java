package ccxh.top.api.task;

import ccxh.top.blog.github.mapper.MarkdownPageMapper;
import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.blog.github.mapper.pojo.UserPojo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import java.util.*;

public class InitRepositoryTask implements Runnable {
    private Logger LOGGER = LoggerFactory.getLogger(InitRepositoryTask.class);

    private final static Map<String, String> HEADER = new HashMap<>();
    static {
        HEADER.put("User-Agent", "Awesome-Octocat-App");
        HEADER.put("Tonke", "token b6b6434dddf0a00c5ddce1884aaba642dd1cfcd5");
    }



    private UserPojo user;
    private ThemeMapper themeMapper;
    private MarkdownPageMapper markdownPageMapper;
    private GithubUtil githubUtil;

    public InitRepositoryTask(UserPojo user, ThemeMapper themeMapper,MarkdownPageMapper markdownPageMapper, GithubUtil githubUtil) {
        this.user = user;
        this.themeMapper = themeMapper;
        this.markdownPageMapper = markdownPageMapper;
        this.githubUtil=githubUtil;
    }

    @Override
    public void run() {
        //获取根目录
        JSONArray themes = githubUtil.getGithubResult("",this.user);
        if (themes == null) {
            return;
        }
        List<ThemePojo> insertList = new ArrayList<>();
        Iterator<Object> iterator = themes.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof JSONObject) {
                JSONObject item = (JSONObject) next;
                //排除非文件夹的东西
                if (!"dir".equals(item.getString("type"))) {
                    continue;
                }
                ThemePojo themePojo = JSON.toJavaObject(item, ThemePojo.class);
                themePojo.setUserId(this.user.getId());
                githubUtil.getDesc(themePojo, user);
                themePojo.quickTime();
                insertList.add(themePojo);
            }
        }
        themeMapper.bathInsert(insertList);
        crateMarkdownPage(insertList);
        LOGGER.info("接入--- github用户名:{},仓库:{},主题:{}",user.getGithubName(),user.getGithubRepot(),insertList.size());
    }




    /**
     * 创建markdownPage
     * @param
     */
    private void crateMarkdownPage(List<ThemePojo> themes) {
        for (ThemePojo themePojo : themes) {
            JSONArray content  = githubUtil.getGithubResult(themePojo.getPath(),this.user);
            if (content  != null) {
                Iterator<Object> iterator = content.iterator();
                ThemePojo paren = themeMapper.selectOne(themePojo);
                while (iterator.hasNext()) {
                    JSONObject item = (JSONObject) iterator.next();
                    if (item.getString("type").equals("dir")
                            || "README.md".equals(item.getString("name"))
                            || item.getString("name").indexOf(".md") == -1) {
                        continue;
                    }
                    MarkdownPagePojo markdownPage = this.githubUtil.generateMarkdownHTML(themePojo,item,this.user);
                    if (markdownPage == null) {
                        continue;
                    }
                    markdownPage.setThemeId(paren.getId());
                    markdownPageMapper.insert(markdownPage);
                }
            }
        }
    }
}
