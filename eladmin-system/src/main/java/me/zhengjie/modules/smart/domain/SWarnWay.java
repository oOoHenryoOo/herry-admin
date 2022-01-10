package me.zhengjie.modules.smart.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author lhl
* @date 2020-01-03
*/
@Entity
@Data
@Table(name="s_warn_way")
public class SWarnWay implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 任务名称
    @Column(name = "warnid")
    private String warnid;

    // 控制器ID
    @Column(name = "deviceid")
    private String deviceid;

    // 支路名称
    @Column(name = "wayname")
    private String wayname;

    // 支路
    @Column(name = "way")
    private String way;

    // 支路状态
    @Column(name = "status")
    private String status;

    // 是否启用
    @Column(name = "valid")
    private String valid;

    // 智能控制属性
    @Column(name = "flag")
    private String flag;

    // 创建时间
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    // 更新时间
    @Column(name = "update_time")
    @UpdateTimestamp
    private Timestamp updateTime;

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SWarnWay source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}