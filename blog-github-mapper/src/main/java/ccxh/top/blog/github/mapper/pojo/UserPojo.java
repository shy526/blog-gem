package ccxh.top.blog.github.mapper.pojo;

import ccxh.top.mapper.core.pojo.BasePojo;

import javax.persistence.Table;

@Table(name = "t_user")
public class UserPojo extends BasePojo {
    /**
     * 密码
     */
    private String pass;
    /**
     * 名称
     */
    private String name;
    /**
     * github用户名
     */
    private String githubName;
    /**
     * github仓库名
     */
    private String githubRepot;
    /**
     * 仓库描述
     */
    private String repotDes;
    private String  access;

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }

    public String getGithubName() {
        return githubName;
    }

    public String getGithubRepot() {
        return githubRepot;
    }

    public String getRepotDes() {
        return repotDes;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGithubName(String githubName) {
        this.githubName = githubName;
    }

    public void setGithubRepot(String githubRepot) {
        this.githubRepot = githubRepot;
    }

    public void setRepotDes(String repotDes) {
        this.repotDes = repotDes;
    }
}
