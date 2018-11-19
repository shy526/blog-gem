package ccxh.top.api.service;

import com.alibaba.fastjson.JSONObject;

public interface GitHookesService {
    void parse(JSONObject object ,String access);
}
