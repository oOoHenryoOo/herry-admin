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
* @date 2019-10-23
*/
@Entity
@Data
@Table(name="s_device")
public class SDevice implements Serializable {

    // 主键
    @Id
    @Column(name = "id")
    private String id;

    // 所属公司
    @Column(name = "company")
    private String company;

    // IMEI码
    @Column(name = "imei")
    private String imei;

    // 电信编码
    @Column(name = "telecomCode")
    private String telecomcode;

    // 设备id
    @Column(name = "deviceid")
    private String deviceid;

    // 监测设备类型
    @Column(name = "devicetype")
    private String devicetype;

    // 设备名称
    @Column(name = "name")
    private String name;

    // 描述
    @Column(name = "nt")
    private String nt;

    // 设备类型 0:nb 1:4g
    @Column(name = "type")
    private String type;

    // 安装位置
    @Column(name = "address")
    private String address;

    // 经度
    @Column(name = "jd")
    private Double jd;

    // 纬度
    @Column(name = "wd")
    private Double wd;

    // 采集频率
    @Column(name = "timetrack")
    private Integer timetrack;

    // 上传频率
    @Column(name = "timetrack2")
    private Integer timetrack2;

    // 信号强度
    @Column(name = "rssi")
    private String rssi;

    // 电池电量
    @Column(name = "battery")
    private String battery;

    // 是否有效
    @Column(name = "valid")
    private String valid;

    // ICCID或卡号
    @Column(name = "phoneCode")
    private String phonecode;

    // ip
    @Column(name = "ip")
    private String ip;

    // 端口
    @Column(name = "port")
    private String port;

    // 监测项目
    @Column(name = "subject")
    private String subject;

    // 监测项目代码
    @Column(name = "code")
    private String code;

    // 计算公式
    @Column(name = "gs")
    private String gs;

    // 二维码
    @Column(name = "qrcode")
    private String qrcode;

    // 工作模式 0手动1自动
    @Column(name = "workmode")
    private String workmode;

    // 在线时间
    @Column(name = "onlinetime")
    private Timestamp onlinetime;

    // 在线
    @Column(name = "online")
    private String online;

    // 控制器
    @Column(name = "relay")
    private String relay;

    // 创建时间
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    // 更新时间
    @Column(name = "update_time")
    @UpdateTimestamp
    private Timestamp updateTime;

    public void copy(SDevice source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}