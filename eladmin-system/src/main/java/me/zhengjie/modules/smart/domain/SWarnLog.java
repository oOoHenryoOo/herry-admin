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
* @date 2020-01-03
*/
@Entity
@Data
@Table(name="s_warn_log")
public class SWarnLog implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 报警名称
    @Column(name = "warnid")
    private String warnid;

    // 报警类型
    @Column(name = "warntype")
    private String warntype;

    // 报警时间
    @Column(name = "warntime")
    private Timestamp warntime;

    // 报警设备
    @Column(name = "deviceid")
    private String deviceid;

    // 报警设备检测项
    @Column(name = "item")
    private String item;

    // 报警时的值
    @Column(name = "warnvalue")
    private String warnvalue;

    // 控制器
    @Column(name = "relayid")
    private String relayid;

    // 指令信息
    @Column(name = "commond")
    private String commond;

    // 执行时间
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    // 支路开关信息
    @Column(name = "wayinfo")
    private String wayinfo;

    // 执行状态
    @Column(name = "msg")
    private String msg;

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SWarnLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}