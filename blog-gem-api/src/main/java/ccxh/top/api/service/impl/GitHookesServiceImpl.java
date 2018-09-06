package ccxh.top.api.service.impl;

import ccxh.top.api.service.GitHookesService;
import ccxh.top.api.task.*;
import ccxh.top.api.util.ThreadPoolUtil;
import ccxh.top.blog.github.mapper.MarkdownPageMapper;
import ccxh.top.blog.github.mapper.ThemeMapper;
import ccxh.top.blog.github.mapper.UserMapper;
import ccxh.top.blog.github.mapper.pojo.UserPojo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author admin
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class GitHookesServiceImpl implements GitHookesService {
   private final  static  ThreadPoolExecutor THREADPOOL = ThreadPoolUtil.getThreadPool();
    @Autowired
    private ThemeMapper themeMapper;
    @Autowired
    private MarkdownPageMapper markdownPageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GithubUtil githubUtil;
    @Value("${path.markdown.root}")
    private String markdownRootPath;
    @Override
    public void parse(JSONObject object,String access) {
        UserPojo condition=new UserPojo();
        condition.setAccess(access);
        UserPojo dbUser = userMapper.selectOne(condition);
        UserPojo githubUser = githubUser(object);
        if ( githubUser==null||dbUser==null
                    ||!githubUser.getGithubName().equals(dbUser.getGithubName())
                    ||!githubUser.getGithubRepot().equals(dbUser.getGithubRepot())){
            return;
        }
        dbUser.setRepotDes(githubUser.getRepotDes());
        dbUser.setUpdateTime(System.currentTimeMillis());
        JSONObject hook = object.getJSONObject("hook");
        dbUser.setUpdateTime(System.currentTimeMillis());
        if (hook!=null){
            userMapper.updateByPrimaryKeySelective(dbUser);
            THREADPOOL.execute(new InitRepositoryTask(dbUser,themeMapper,markdownPageMapper,githubUtil));
        }else {
            //处理主要逻辑
            userMapper.updateByPrimaryKeySelective(dbUser);
            JSONArray commits = object.getJSONArray("commits");
            Iterator<Object> iterator = commits.iterator();
            while (iterator.hasNext()){
                JSONObject  item = (JSONObject)iterator.next();
                JSONArray added = item.getJSONArray("added");
                if (added!=null&&added.size()>0){
                    THREADPOOL.execute(new MarkdownAdded(added,dbUser,githubUtil,themeMapper,markdownPageMapper));
                }
                JSONArray removed = item.getJSONArray("removed");
                if (removed!=null&&removed.size()>0){
                    THREADPOOL.execute(new MarkdownRemoved(removed,dbUser,markdownRootPath,markdownPageMapper,themeMapper,githubUtil));
                }
                JSONArray modified = item.getJSONArray("modified");
                if (modified!=null&&modified.size()>0){
                    THREADPOOL.execute(new MarkdownModifiedTask(modified,dbUser,this.markdownRootPath,githubUtil,markdownPageMapper,themeMapper));
                }
            }
            System.out.println("modified = ");
        }





    }

    private UserPojo githubUser(JSONObject object){
        JSONObject repository = object.getJSONObject("repository");
        if(repository==null){ return null;}
        UserPojo userPojo = new UserPojo();
        userPojo.setRepotDes( repository.getString("description"));
        String[] split = repository.getString("full_name").split("/");
        //用户名
        userPojo.setGithubName(split[0]);
        userPojo.setGithubRepot(split[1]);
        userPojo.quickTime();
        return userPojo;
    }
}
