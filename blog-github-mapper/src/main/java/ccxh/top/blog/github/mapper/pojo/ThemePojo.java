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
     * 唯一sha
     */
    private String sha;
    /**
     * github url
     */
    private String url;

    /**
     * 父目录
     */
    private Integer parent;

    /**
     * 描述
     */
    private String des;

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

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
