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
* @date 2019-11-14
*/
@Entity
@Data
@Table(name="s_task")
public class STask implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 任务名称
    @Column(name = "name")
    private String name;

    // 任务类型
    @Column(name = "tasktype")
    private String tasktype;

    // 任务时间
    @Column(name = "tasktime")
    private String tasktime;

    // 控制器
    @Column(name = "deviceid")
    private String deviceid;

    // 指令信息
    @Column(name = "commond")
    private String commond;

    // 支路开关信息
    @Column(name = "wayinfo")
    private String wayinfo;

    // 是否启用
    @Column(name = "valid")
    private String valid;

    // 用户
    @Column(name = "userid")
    private String userid;

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

    // cron 表达式
    @Column(name = "cron")
    private String cron;

    public void copy(STask source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}