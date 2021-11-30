package me.zhengjie.modules.smart.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.bson.types.ObjectId;

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
@Table(name="s_data")
public class SData implements Serializable {

    // 主键
    @Id
    @Column(name = "id")
    private String id;

    // 设备id
    @Column(name = "deviceId")
    private String deviceid;

    // 检测项
    @Column(name = "item")
    private String item;

    // 时间
    @Column(name = "dtime")
    private String dtime;

    // 数据值
    @Column(name = "data")
    private String data;

    // 信号强度
    @Column(name = "rssi")
    private String rssi;

    // 电量
    @Column(name = "battery")
    private String battery;

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SData source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}