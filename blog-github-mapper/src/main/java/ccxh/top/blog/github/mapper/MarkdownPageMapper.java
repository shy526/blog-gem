package ccxh.top.blog.github.mapper;

import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.mapper.core.SysMapper;

/**
 * 管理文章
 * @author admin
 */
public interface MarkdownPageMapper extends SysMapper<MarkdownPagePojo> {
    /**
     * 顺带插入 themeId
     * @param item
     * @return
     */
    Integer insertThemeId(MarkdownPagePojo item);
}
