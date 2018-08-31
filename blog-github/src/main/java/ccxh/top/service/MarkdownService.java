package ccxh.top.service;

import ccxh.top.pojo.Result;

/**
 * markdown 借口
 * @author admin
 */
public interface MarkdownService {
    /**
     * 获取markdown page duixiang
     * @param themeId
     * @return
     */
     Result getMarkdownPageBy(Integer themeId);
}
