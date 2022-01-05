package me.zhengjie.modules.smart.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author lhl
* @date 2019-10-22
*/
@Entity
@Data
@Table(name="s_warn")
public class SWarn implements Serializable {

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

    // 报警类型
    @Column(name = "warntype")
    private String warntype;

    // 开关状态
    @Column(name = "state")
    private String state;

    // 报警值下限
    @Column(name = "min")
    private BigDecimal min;

    // 报警值上限
    @Column(name = "max")
    private BigDecimal max;

    // 超下限控制器
    @Column(name = "relayidmin")
    private String relayidmin;

    // 超上限控制器
    @Column(name = "relayidmax")
    private String relayidmax;

    // 报警名称
    @Column(name = "name")
    private String name;

    // 报警消息
    @Column(name = "msg")
    private String msg;

    // 是否启用
    @Column(name = "enable")
    private String enable;

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

    public void copy(SWarn source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}