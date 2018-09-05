package ccxh.top.blog.github.mapper;

import ccxh.top.blog.github.mapper.pojo.MarkdownPagePojo;
import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.mapper.core.SysMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 根据主题id 查询markdown
     * @param id
     * @return
     */
    List<MarkdownPagePojo> selectMarkdownPageByThemeId(Integer id);

    /**
     * 根据themeid 删除markdwon
     * @param id
     * @return
     */
    Integer deleteMarkdownPageByThemeId(Integer id);

    /**
     * 根据themeid 删除markdwon
     * @param list
     * @return
     */
    Integer bathDeleteMarkdownPageByThemeId(List<ThemePojo> list);

    Integer updateTimeById(@Param("id") Integer id, @Param("time") long time);
}
