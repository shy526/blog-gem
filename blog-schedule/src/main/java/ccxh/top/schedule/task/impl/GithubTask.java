package ccxh.top.schedule.task.impl;

import ccxh.top.blog.github.mapper.MarkdownPageMapper;
import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.schedule.task.Task;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import java.net.SocketTimeoutException;
import java.util.*;

@Component
public class GithubTask implements Task  {
    private final static Logger logger = LoggerFactory.getLogger(GithubTask.class);
    private final static String REONPS_CONTENS = "https://api.github.com/repos/%s/%s/contents/%s";
    private final static String README = "/README.md";
    private final static BASE64Decoder decoder = new BASE64Decoder();
    private final static Map<String, String> HEANDS = new HashMap<>();
    /**
     *    读取模板的地址
     */
/*    @Value("${path.template}")
    private   String TEMPLATE_PATH;*/
    /**
     *     生成后的保存地址
     */
    @Value("${path.markdown.root}")
    private  String MARKDOWN_ROOT_PATH;
    static {
        HEANDS.put("User-Agent", "Awesome-Octocat-App");
        HEANDS.put("Tonke", "token b6b6434dddf0a00c5ddce1884aaba642dd1cfcd5");
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
    @Scheduled(cron = "0 0/1 * * * ?")
    public void dispatch() {
        List<ThemePojo> list = new ArrayList<>();
        try {
            String result = httpClientService.doGetSetHaeader(String.format(REONPS_CONTENS, "sunjiaqing", "testNote", ""), HEANDS);
            Object object = JSON.parse(result);
            if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) object;
                Iterator<Object> iterator = jsonArray.iterator();
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    if (next instanceof JSONObject) {
                        JSONObject next1 = (JSONObject) next;
                        if (!next1.getString("type").equals("dir")) {
                            continue;
                        }
                        ThemePojo themePojo = JSON.toJavaObject(next1, ThemePojo.class);
                        themePojo.setUserId(0);
                        this.getDesc(themePojo);
                        themePojo.quickTime();
                        list.add(themePojo);
                    }
                }
            }
            //没有找到的情况
            else if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                Object message = jsonObject.get("message");
                if (message != null) {
                    logger.info(message.toString());
                }
            } else {
                logger.info("Type not find");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disposeTheme(list);

    }

    /**
     * 获取主题描述
     *
     * @param themePojo
     */
    private void getDesc(ThemePojo themePojo) {
        try {
            String s = httpClientService.doGetSetHaeader(String.format(REONPS_CONTENS, "sunjiaqing", "testNote", themePojo.getPath().concat(README))
                    , HEANDS);
            if (StringUtil.isEmpty(s)) {
                return;
            }
            JSONObject jsonObject = JSON.parseObject(s);
            String content = jsonObject.getString("content");
            if (StringUtil.isNotEmpty(content)) {
                String s1 = new String(decoder.decodeBuffer(content), "UTF-8");
                themePojo.setDes(s1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        if (insertList.size()>0){
          themeMapper.bathInsert(insertList);
        }
        if (db.size()>0) {
            themeMapper.bathDeleteById(db);
        }
        for (ThemePojo u : updateList) {
            //TODO: 以后修改为批量操作
            themeMapper.updateByPrimaryKey(u);
        }
        //合并新增和更新的
        crateMarkdownPage(insertList);
    }

    /**
     * 创建markdownPage
     */
    private void crateMarkdownPage(List<ThemePojo> list) {
        for (ThemePojo themePojo : list) {
            try {
                String result = httpClientService.doGetSetHaeader(String.format(REONPS_CONTENS, "sunjiaqing", "testNote", themePojo.getPath()), HEANDS);
                Object object = JSON.parse(result);
                if (object instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) object;
                    Iterator<Object> iterator = jsonArray.iterator();
                    while (iterator.hasNext()) {

                        JSONObject next = (JSONObject) iterator.next();
                        if (next.getString("type").equals("dir")
                                ||"README.md".equals(next.getString("name"))
                                || next.getString("name").indexOf(".md") == -1) {
                            continue;
                        }
                        MarkdownPagePojo markdownPage = getMarkdownPage(themePojo, next);
                        Document githubPage = Jsoup.connect(markdownPage.getUrl()).get();
                        String htmlPage = githubPage.body().select("article").html();
                        Document nowPage = Jsoup.parse( ResourceUtils.getFile("classpath:markdown_template.html"), "utf-8");
                        nowPage.body().select(".single-page").append(htmlPage);
                        markdownPage.setLocalPath(MARKDOWN_ROOT_PATH + "/sunjiaqing" + "/testNote/"+markdownPage.getPath() + ".html");
                        File file = new File(markdownPage.getLocalPath()).getParentFile();
                       if (!file.exists()){
                           file.mkdirs();
                       }

                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                new FileOutputStream(markdownPage.getLocalPath()));
                        bufferedOutputStream.write(nowPage.outerHtml().getBytes());
                        bufferedOutputStream.flush();

                        int insert = markdownPageMapper.insertThemeId(markdownPage);
                    }
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }


    /**
     * 组装基本markdownPagePojo 对象
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
        return  page;
    }

    /**
     * 筛选相同的path
     *
     * @param db
     * @param now
     * @return
     */
    private void allotTheme(List<ThemePojo> db, ThemePojo now, List<ThemePojo> updateList, List<ThemePojo> insertList) {
        Integer deleteIdex = null;
        for (int i = 0; i < db.size(); i++) {
            if (db.get(i).getPath().equals(now.getPath())) {
                deleteIdex = i;
                break;
            }
        }
        if (deleteIdex != null) {
            ThemePojo themePojo = db.get(deleteIdex);
            db.remove(deleteIdex.intValue());
            System.out.println("themePojo = " + db.size());
            //说明要更新
            if (!themePojo.getSha().equals(now.getSha())) {
                now.setUserId(themePojo.getUserId());
                now.setId(themePojo.getId());
                now.setUpdateTime(System.currentTimeMillis());
                updateList.add(now);
            }
        } else {
            // now 是新增加的主题
            insertList.add(now);
        }

    }
}
