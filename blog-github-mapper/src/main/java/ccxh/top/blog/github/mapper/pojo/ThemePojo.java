package ccxh.top.blog.github.mapper.pojo;

import ccxh.top.mapper.core.pojo.BasePojo;

import javax.persistence.Table;

/**
 * 目录主题类
 * @author ccxh
 */
@Table(name="t_blog_theme")
public class ThemePojo extends BasePojo {
    /**
     * 名称
     */
    private String name;
    /**
     * 目录
     */
    private String path;

    /**
     * 父目录
     */
    private Integer parent;

    /**
     * 描述
     */
    private String des;

    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
