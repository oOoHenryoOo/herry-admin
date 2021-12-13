package me.zhengjie.modules.smart.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author lhl
* @date 2019-10-30
*/
@Entity
@Data
@Table(name="s_device_log")
public class SDeviceLog implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 设备ID
    @Column(name = "deviceid")
    private String deviceid;

    // 用户ID
    @Column(name = "userid")
    private String userid;

    // 控制
    @Column(name = "action")
    private String action;

    // 操作结果
    @Column(name = "message")
    private String message;

    // 日志时间
    @Column(name = "logtime")
    @CreationTimestamp
    private Timestamp logtime;

    // 支路
    @Column(name = "way")
    private String way;

    // 支路名称
    @Column(name = "wayname")
    private String wayname;

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SDeviceLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}