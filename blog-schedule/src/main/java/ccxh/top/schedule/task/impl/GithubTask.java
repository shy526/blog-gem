package ccxh.top.schedule.task.impl;

import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.schedule.task.Task;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import tk.mybatis.mapper.util.StringUtil;
import top.ccxh.httpclient.service.HttpClientService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Component
public class GithubTask implements Task {
    private final static Logger logger=LoggerFactory.getLogger(GithubTask.class);
    private final  static String REONPS_CONTENS = "https://api.github.com/repos/%s/%s/contents/%s";
    private final  static String README = "/README.md";
   private  final static BASE64Decoder  decoder = new BASE64Decoder();
   private  final static  Map<String,String> HEANDS=new HashMap<>();;
   static {
       HEANDS.put("User-Agent","Awesome-Octocat-App");
       HEANDS.put("Tonke","token b6b6434dddf0a00c5ddce1884aaba642dd1cfcd5");
   }
    @Autowired
    private HttpClientService httpClientService;
   @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
   @Autowired
   private ThemeMapper themeMapper;
    @Override
    @Scheduled(cron = "0 0/50 * * * ?")
    public void dispatch() {
        List<ThemePojo> list=new ArrayList<>();
        try {
            String result = httpClientService.doGetSetHaeader(String.format(REONPS_CONTENS, "sunjiaqing", "testNote",""),HEANDS);
            Object object = JSON.parse(result);
            if(object instanceof JSONArray){
                JSONArray jsonArray=(JSONArray)object;
                Iterator<Object> iterator = jsonArray.iterator();
                while (iterator.hasNext()){
                    Object next = iterator.next();
                    if (next instanceof JSONObject){
                        JSONObject next1 =(JSONObject) next;
                        if (!next1.getString("type").equals("dir") ){
                            continue;
                        }
                        ThemePojo themePojo = JSON.toJavaObject(next1, ThemePojo.class);
                        this.getDesc(themePojo);
                        themePojo.quickTime();
                        list.add(themePojo);
                    }
                }
            }else if (object instanceof JSONObject){
                JSONObject jsonObject=(JSONObject)object;
                Object message= jsonObject.get("message");
                if (message!=null){
                    logger.info(message.toString());
                }
            }else {
                logger.info("Type not find");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size()>0){
            themeMapper.bathInsert(list);
        }
    }

    /**
     * 获取主题描述
     * @param themePojo
     */
    private void getDesc(ThemePojo themePojo)  {
        try {
            String s = httpClientService.doGetSetHaeader(String.format(REONPS_CONTENS, "sunjiaqing", "testNote", themePojo.getPath().concat(README))
                    , HEANDS);
            if (StringUtil.isEmpty(s)){
                return ;
            }
            JSONObject jsonObject = JSON.parseObject(s);
            String content = jsonObject.getString("content");
            if(StringUtil.isNotEmpty(content)){
                String s1 = new String(decoder.decodeBuffer(content), "UTF-8");
                themePojo.setDes(s1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
