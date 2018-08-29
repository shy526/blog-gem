package ccxh.top.blog.github.mapper.pojo;

import ccxh.top.mapper.core.pojo.BasePojo;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 目录主题类
 * @author ccxh
 */
@Table(name="t_blog_genm_page")
public class MarkdownPagePojo extends BasePojo {
    /**
     * 名称
     */
    private String name;
    /**
     * sha
     */
    private String sha;
    /**
     * 物理大小
     */
    private Long size;
    /**
     * 主题
     */
    private Integer themeId;
    /**
     * 存储物理地址
     */
    private String localPath;
    /**
     * GitHub 的原链接
     */
    private String url;
    /**
     * 展示时用的 链接
     */
    private String showUrl;
    /**
     * github 提供的下载链接
     */
    private String downloadUrl;
    /**
     * github服务器路径
     */
    private String path;

    @Transient
    private String themeSha;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThemeSha() {
        return themeSha;
    }

    public void setThemeSha(String themeSha) {
        this.themeSha = themeSha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getThemeId() {
        return themeId;
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(String showUrl) {
        this.showUrl = showUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
