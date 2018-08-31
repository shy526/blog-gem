package ccxh.top.blog.github.mapper.pojo;

import javax.persistence.Table;

@Table(name = "t_user")
public class UserPojo {
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
}
