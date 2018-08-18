package ccxh.top.mapper.core.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 基础pojo类
 * @author ccxh
 */
public class BasePojo implements Serializable {
    private static final long serialVersionUID = 7L;
    private Long createTime;
    private Long updateTime;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 快速设置时间参数
     */
    public void quickTime(){
        this.createTime=System.currentTimeMillis();
        this.updateTime=createTime;
    }
}
