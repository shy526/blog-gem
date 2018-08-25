package ccxh.top.service;

import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.pojo.Result;

import java.util.List;

public interface ThemeService {
    /**
     * 分页模型
     * @param pageIndex
     * @param num
     * @return
     */
    Result thenmePage(Integer  pageIndex, Integer num);
}
