package ccxh.top.schedule.task.impl;

import ccxh.top.blog.github.mapper.MarkdownPageMapper;
import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.schedule.task.Task;
import ccxh.top.schedule.util.IOUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import sun.misc.BASE64Decoder;
import tk.mybatis.mapper.util.StringUtil;
import top.ccxh.httpclient.service.HttpClientService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 这个以后可能会重写 利用webhook 去做
 * github 数据获取的定时任务
 * @author admin
 */
@Component
public class GithubTask implements Task {
    private final static Logger logger = LoggerFactory.getLogger(GithubTask.class);
    private final static String DEPOT_CONTENTS = "https://api.github.com/repos/%s/%s/contents/%s";
    private final static String README = "/README.md";
    private final static BASE64Decoder DECODER = new BASE64Decoder();
    private final static Map<String, String> HEADER = new HashMap<>();
    /**
     * 生成后的保存地址
     */
    @Value("${path.markdown.root}")
    private String markdownRootPath;
    @Value("${path.html}")
    private String showBasePath;

    static {
        HEADER.put("User-Agent", "Awesome-Octocat-App");
        HEADER.put("Tonke", "token b6b6434dddf0a00c5ddce1884aaba642dd1cfcd5");
    }

    @Autowired
    private HttpClientService httpClientService;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ThemeMapper themeMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MarkdownPageMapper markdownPageMapper;


    @Override
    @Scheduled(cron = "0 0/50 * * * ?")
    public void dispatch() {
        List<ThemePojo> list = new ArrayList<>();
        JSONArray jsonArray = getGithubResult("");
        if (jsonArray == null) {
            return;
        }
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof JSONObject) {
                JSONObject next1 = (JSONObject) next;
                if (!"dir".equals(next1.getString("type"))) {
                    continue;
                }
                ThemePojo themePojo = JSON.toJavaObject(next1, ThemePojo.class);
                themePojo.setUserId(0);
                this.getDesc(themePojo);
                themePojo.quickTime();
                list.add(themePojo);
            }
        }

        disposeTheme(list);

    }

    /**
     * 从gethub中获取数据
     * @param githubPath
     * @return JSONArray
     */
    private JSONArray  getGithubResult(String githubPath) {
        String result = null;
        try {
            result = httpClientService.doGetSetHaeader(String.format(DEPOT_CONTENTS, "sunjiaqing", "blog-note", githubPath), HEADER);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (result== null || "".equals(result)) { logger.info("result is null"); return null ; }
            Object object = JSON.parse(result);
            if (object instanceof JSONArray) {
                return (JSONArray) object;
            } else if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                Object message = jsonObject.get("message");
                if (message != null) {
                    logger.info(message.toString());
                }
            } else {
                logger.info("Type not find");
            }

        return null;
    }

    /**
     * 获取主题描述
     *
     * @param themePojo
     */
    private void getDesc(ThemePojo themePojo) {
        try {
            String s = httpClientService.doGetSetHaeader(String.format(DEPOT_CONTENTS, "sunjiaqing", "blog-note", themePojo.getPath().concat(README))
                    , HEADER);
            if (StringUtil.isEmpty(s)) {
                return;
            }
            JSONObject jsonObject = JSON.parseObject(s);
            String content = jsonObject.getString("content");
            if (StringUtil.isNotEmpty(content)) {
                String s1 = new String(DECODER.decodeBuffer(content), "UTF-8");
                themePojo.setDes(s1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分配 更新 删除 和新建立的主题
     * @param now
     */
    private void disposeTheme(List<ThemePojo> now) {
        if (now.size() <= 0) {
            return;
        }
        List<ThemePojo> updateList = new ArrayList<>();
        List<ThemePojo> insertList = new ArrayList<>();
        ThemePojo condition = new ThemePojo();
        condition.setUserId(0);
        List<ThemePojo> db = themeMapper.select(condition);
        for (ThemePojo t : now) {
            allotTheme(db, t, updateList, insertList);
        }
        if (insertList.size() > 0) {

            themeMapper.bathInsert(insertList);
        }
        if (db.size() > 0) {
            themeMapper.bathDeleteById(db);
            markdownPageMapper.bathDeleteMarkdownPageByThemeId(db);
        }
        for (ThemePojo u : updateList) {
            //TODO: 以后修改为批量操作
            themeMapper.updateByPrimaryKey(u);
        }
        //合并新增和更新的
        logger.info("insert:{},delete:{},update:{}", insertList.size(), db.size(), updateList.size());
        crateMarkdownPage(insertList);
        deleteMarkdownPage(db);
        updateMarkdownPage(updateList);
    }

    /**
     * 更新主题下的markown
     * @param updateList
     */
    private void updateMarkdownPage(List<ThemePojo> updateList) {
        int delete=0;
        int update=0;
        int insert=0;
        for (ThemePojo themePojo : updateList) {
            File file = Paths.get(markdownRootPath,"sunjiaqing", "blog-note", themePojo.getPath()).toFile();
            //文件不存在 跳过
            if (!file.exists() || !file.isDirectory()) {
                continue;
            }
            try {
                  JSONArray result = this.getGithubResult(themePojo.getPath());
                if (result == null) {
                    continue;
                }
                Iterator<Object> iterator = result.iterator();
                List<MarkdownPagePojo> markdownPagePojos = markdownPageMapper.selectMarkdownPageByThemeId(themePojo.getId());

                while (iterator.hasNext()){
                    JSONObject next = (JSONObject) iterator.next();
                    if (next.getString("type").equals("dir")
                            || "README.md".equals(next.getString("name"))
                            || next.getString("name").indexOf(".md") == -1) {
                        continue;
                    }
                    Iterator<MarkdownPagePojo> pageIterator = markdownPagePojos.iterator();
                    boolean flag=true;
                    while(pageIterator.hasNext()){
                        MarkdownPagePojo pagePojo = pageIterator.next();
                        if (pagePojo.getPath().equals(next.getString("path"))){
                            flag=false;
                            pageIterator.remove();
                            //这里说明文件更新
                            if (!next.getString("sha").equals(pagePojo.getSha())){
                                MarkdownPagePojo pagePojo1 = generateMarkdownHTML(themePojo,next);
                                pagePojo1.setId(pagePojo.getId());
                                markdownPageMapper.updateByPrimaryKey(pagePojo1);
                                update++;
                            }
                        }
                    }
                    if (flag){
                        //说明文件是新增加的
                        insert++;
                        markdownPageMapper.insertThemeId(generateMarkdownHTML(themePojo,next));
                    }
                }
                //剩余的说明 远程已经删除
                for (MarkdownPagePojo mark:markdownPagePojos){
                    markdownPageMapper.delete(mark);
                    new File(Paths.get(mark.getLocalPath()).toString()).delete();
                }
                delete+=markdownPagePojos.size();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        logger.info("update total insert:{},delete:{},update:{}", insert, delete,update);
    }

    /**
     * 某个主题被删除
     *
     * @param db
     */
    private void deleteMarkdownPage(List<ThemePojo> db) {
        for (ThemePojo themePojo : db) {
            Path fixation = Paths.get(markdownRootPath, "sunjiaqing", "blog-note", themePojo.getPath());
            File file = fixation.toFile();
            if (!file.exists()) {
                return;
            }
            boolean b = IOUtil.deleteDir(fixation.toString());
        }
    }

    /**
     * 创建markdownPage
     *
     * @param
     */
    private void crateMarkdownPage(List<ThemePojo> list) {
        for (ThemePojo themePojo : list) {
            JSONArray jsonArray = getGithubResult(themePojo.getPath());
                if (jsonArray!=null) {
                    Iterator<Object> iterator = jsonArray.iterator();
                    while (iterator.hasNext()) {
                        JSONObject next = (JSONObject) iterator.next();
                        if (next.getString("type").equals("dir")
                                || "README.md".equals(next.getString("name"))
                                || next.getString("name").indexOf(".md") == -1) {
                            continue;
                        }
                        MarkdownPagePojo markdownPage = generateMarkdownHTML(themePojo, next);
                        if (markdownPage == null){ continue;}
                        markdownPageMapper.insertThemeId(markdownPage);
                    }
                }
        }
    }

    /**
     * 生成 markdown html
     * @param themePojo 主题对象
     * @param next
     * @return
     */
    private MarkdownPagePojo generateMarkdownHTML(ThemePojo themePojo, JSONObject next)  {
        MarkdownPagePojo markdownPage = getMarkdownPage(themePojo, next);
        try {
            Document githubPage = Jsoup.connect(markdownPage.getUrl()).get();
            String htmlPage = githubPage.body().select("#readme").html();
            Document nowPage = Jsoup.parse(IOUtil.readJarFileString("/markdown_template.html"));
            nowPage.body().select(".single-page").append(htmlPage);
            nowPage.title(markdownPage.getName());
            //基础路径
            Path fixation = Paths.get("sunjiaqing", "blog-note",markdownPage.getPath()+ ".html");
            markdownPage.setLocalPath(Paths.get(markdownRootPath, fixation.toString()).toString());
            markdownPage.setShowUrl(Paths.get(showBasePath ,fixation.toString()).toString());
            File file = new File(markdownPage.getLocalPath()).getParentFile();
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }
            //语法糖需要 那啥
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(markdownPage.getLocalPath()))) {
                bufferedOutputStream.write(nowPage.outerHtml().getBytes());
                bufferedOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return markdownPage;
    }


    /**
     * 组装基本markdownPagePojo 对象
     *
     * @param themePojo
     * @param next
     * @return
     */
    private MarkdownPagePojo getMarkdownPage(ThemePojo themePojo, JSONObject next) {
        MarkdownPagePojo page = JSON.toJavaObject(next, MarkdownPagePojo.class);
        page.setDownloadUrl(next.getString("download_url"));
        page.setUrl(next.getString("html_url"));
        page.setThemeSha(themePojo.getSha());
        page.quickTime();
        return page;
    }

    /**
     * 筛选相同的path
     *
     * @param db
     * @param now
     * @return
     */
    private void allotTheme(List<ThemePojo> db, ThemePojo now, List<ThemePojo> updateList, List<ThemePojo> insertList) {
        Iterator<ThemePojo> iterator = db.iterator();
        //删除要有迭代器 否则 下次迭代 时抛出java.util.ConcurrentModificationException: null
        while (iterator.hasNext()) {
            ThemePojo next = iterator.next();
            if (next.getPath().equals(now.getPath())) {
                //相同的主题就删除
                iterator.remove();
                if (!next.getSha().equals(now.getSha())) {
                    //更新的
                    now.setUserId(next.getUserId());
                    now.setId(next.getId());
                    now.setUpdateTime(System.currentTimeMillis());
                    updateList.add(now);
                }
                return;
            }
        }
        // 走完 说明 没有相同的主题,now 是新增加的主题
        insertList.add(now);

    }
}
