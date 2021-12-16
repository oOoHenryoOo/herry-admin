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
@Table(name="s_item")
public class SItem implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 检测项代码
    @Column(name = "code")
    private String code;

    // 检测项名称
    @Column(name = "subject")
    private String subject;

    // 检测项计算公式
    @Column(name = "gs")
    private String gs;

    // 是否有效
    @Column(name = "valid")
    private String valid;

    // 创建时间
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    // 更新时间
    @Column(name = "update_time")
    @UpdateTimestamp
    private Timestamp updateTime;

    // 检测项单位
    @Column(name = "unit")
    private String unit;

    public void copy(SItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}