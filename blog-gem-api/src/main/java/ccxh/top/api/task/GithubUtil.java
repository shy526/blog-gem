package ccxh.top.api.task;

import ccxh.top.api.util.IOUtil;
import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.blog.github.mapper.pojo.UserPojo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.catalina.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import tk.mybatis.mapper.util.StringUtil;
import top.ccxh.httpclient.service.HttpClientService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

@Component
public class GithubUtil {
    private Logger LOGGER = LoggerFactory.getLogger(GithubUtil.class);
    @Value("${path.markdown.root}")
    private String markdownRootPath;
    @Value("${path.html}")
    private String showBasePath;
    @Autowired
    private HttpClientService httpClientService;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ThemeMapper themeMapper;
    private final static Map<String, String> HEADER = new HashMap<>();

    static {
        HEADER.put("User-Agent", "Awesome-Octocat-App");
        HEADER.put("Tonke", "token b6b6434dddf0a00c5ddce1884aaba642dd1cfcd5");
    }

    private final static String DEPOT_CONTENTS = "https://api.github.com/repos/%s/%s/contents/%s";
    private final static String GITHUB_PAGE = "https://github.com/%s/%s/blob/master/%s";
    public final static String README = "/README.md";
    private final static BASE64Decoder DECODER = new BASE64Decoder();

    /**
     * MarkdownPagePojo
     *
     * @param themePojo 主题对象
     * @param next      那啥
     * @return
     */
    public MarkdownPagePojo generateMarkdownHTML(ThemePojo themePojo, JSONObject next, UserPojo user) {
        MarkdownPagePojo markdownPage = getMarkdownPage(themePojo, next);
        try {
            outputmarkdown(markdownPage, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return markdownPage;
    }

    private void outputmarkdown(MarkdownPagePojo markdownPage, UserPojo user) throws Exception {
        Document githubPage = Jsoup.connect(markdownPage.getUrl()).get();
        //获取哪个啥
        Elements select = githubPage.body().select("#readme");
        //处理图片 无法显示的问题
        Elements imgs = select.select("img");
        ListIterator<Element> imgIterator = imgs.listIterator();
        URL url = new URL(markdownPage.getUrl());
        while (imgIterator.hasNext()) {
            Element img = imgIterator.next();
            String src = img.attr("src");
            img.attr("src", url.getProtocol() + "://" + url.getHost() + src);
            String href = img.parent().attr("href");
            img.parent().attr("href", url.getProtocol() + "://" + url.getHost() + href);
        }
        String htmlPage = select.html();
        Document nowPage = Jsoup.parse(IOUtil.readJarFileString("/markdown_template.html"));
        nowPage.body().select(".single-page").append(htmlPage);
        nowPage.title(markdownPage.getName());
        //基础路径
        //    Path fixation = Paths.get(user.getGithubName(), user.getGithubRepot(),markdownPage.getPath()+ ".html");
        // markdownPage.setLocalPath(Paths.get(markdownRootPath, fixation.toString()).toString());
        // markdownPage.setShowUrl(Paths.get(showBasePath ,fixation.toString()).toString());
        String fixation = user.getGithubName() + "/" + user.getGithubRepot() + "/" + markdownPage.getPath() + ".html";
        markdownPage.setLocalPath(markdownRootPath + "/" + fixation);
        markdownPage.setShowUrl(showBasePath + "/" + fixation);
        File file = new File(markdownPage.getLocalPath()).getParentFile();
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return;
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
        File back = new File(markdownPage.getLocalPath());
        if (!back.exists()) {
            LOGGER.info("markdown 生成失败:{}", JSON.toJSONString(markdownPage));
        }
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
        page.setUrl(next.getString("html_url"));
        page.quickTime();
        return page;
    }


    /**
     * 从gethub中获取数据
     *
     * @param githubPath
     * @return JSONArray
     */
    public JSONArray getGithubResult(String githubPath, UserPojo userPojo) {
        String result = null;
        try {
            result = httpClientService.doGetSetHaeader(String.format(DEPOT_CONTENTS, userPojo.getGithubName(),
                    userPojo.getGithubRepot(), githubPath), HEADER);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        if (result == null || "".equals(result)) {
            LOGGER.info("result is null");
            return null;
        }
        Object object = JSON.parse(result);
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        } else if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            Object message = jsonObject.get("message");
            if (message != null) {
                LOGGER.info(message.toString());
            }
        } else {
            LOGGER.info("Type not find");
        }

        return null;
    }


    /**
     * 获取主题描述
     *
     * @param themePojo
     */
    public void getDesc(ThemePojo themePojo, UserPojo userPojo) {
        try {
            String s = httpClientService.doGetSetHaeader(String.format(DEPOT_CONTENTS, userPojo.getGithubName(),
                    userPojo.getGithubRepot(), themePojo.getPath().concat(README))
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

    public MarkdownPagePojo createMarkdown(String path, UserPojo user) {
        MarkdownPagePojo page = new MarkdownPagePojo();
        page.setUrl(String.format(GITHUB_PAGE, user.getGithubName(), user.getGithubRepot(), path));
        String[] split = path.split("/");
        if (split.length > 2) {
            return null;
        }
        page.setName(split[1]);
        page.setPath(path);
        try {
            outputmarkdown(page, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }


    /**
     * 父亲
     *
     * @param path 根据path 和userid 确认markdown的主题
     * @return
     */
    public ThemePojo getParentTheme(String path, UserPojo user) {
        String[] split = path.split("/");
        ThemePojo condition = new ThemePojo();
        condition.setUserId(user.getId());
        condition.setPath(split[0]);
        return themeMapper.selectOne(condition);
    }
}
