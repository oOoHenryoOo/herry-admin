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
* @date 2019-11-11
*/
@Entity
@Data
@Table(name="s_sysmanage")
public class SSysmanage implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 公司名称
    @Column(name = "companyname")
    private String companyname;

    // 备案号
    @Column(name = "recordno")
    private String recordno;

    // Logo
    @Column(name = "logoadr")
    private String logoadr;

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

    public void copy(SSysmanage source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}