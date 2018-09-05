package ccxh.top.api.controller;

import ccxh.top.api.service.GitHookesService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.mapper.util.StringUtil;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理webHooks
 *
 * @author admin
 */
@Controller
public class GithubHooksController {
    private final static Logger LOGGER = LoggerFactory.getLogger(GithubHooksController.class);
    @Autowired
    GitHookesService gitHookesService;
    @RequestMapping("/hooks/{access}")
    public void githubHooks(@PathVariable String access,@RequestBody JSONObject object ,HttpServletResponse response) {
        if (StringUtil.isEmpty(access)||object==null){
            return;
        }
        gitHookesService.parse(object,access);
    }

}
