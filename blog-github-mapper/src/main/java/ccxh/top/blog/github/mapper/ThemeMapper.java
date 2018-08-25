package ccxh.top.blog.github.mapper;

import ccxh.top.blog.github.mapper.pojo.ThemePojo;
import ccxh.top.mapper.core.SysMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * t_blog_theme 表操作类
 * @author admin
 */
public interface ThemeMapper extends SysMapper<ThemePojo> {
    /**
     * 批量插入
     * @param themes
     * @return
     */
    Integer bathInsert(List<ThemePojo> themes);

    /**
     * 分页
     * @param backPageNum 初始下标
     * @param num 取的数量
     * @return
     */
    List<ThemePojo> selectThemePage(@Param("index") int backPageNum, @Param("num")Integer num);
}
