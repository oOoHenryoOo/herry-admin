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
* @date 2019-10-22
*/
@Entity
@Data
@Table(name="s_warnmsg")
public class SWarnmsg implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 设备ID
    @Column(name = "deviceid")
    private String deviceid;

    // 检测项
    @Column(name = "item")
    private String item;

    // 报警信息
    @Column(name = "message")
    private String message;

    // 报警状态
    @Column(name = "flg")
    private String flg;

    // 消息发送时间
    @Column(name = "dtime")
    @CreationTimestamp
    private Timestamp dtime;

    // 解除报警时间
    @Column(name = "jtime")
    @UpdateTimestamp
    private Timestamp jtime;

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SWarnmsg source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}