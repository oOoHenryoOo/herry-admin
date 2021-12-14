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
* @date 2019-10-21
*/
@Entity
@Data
@Table(name="s_devicetype")
public class SDevicetype implements Serializable {

    // 主键
    @Id
    @Column(name = "id")
    private Long id;

    // 设备类型名称
    @Column(name = "typename")
    private String typename;

    // 检测项代码
    @Column(name = "code")
    private String code;

    // 检测项名称
    @Column(name = "subject")
    private String subject;

    // 检测项计算公式
    @Column(name = "gs")
    private String gs;

    // 创建时间
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    // 更新时间
    @Column(name = "update_time")
    @UpdateTimestamp
    private Timestamp updateTime;

    public void copy(SDevicetype source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}