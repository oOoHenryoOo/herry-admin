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
@Table(name="s_project")
public class SProject implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 名称
    @Column(name = "proname")
    private String proname;

    // 信息1
    @Column(name = "msg1")
    private String msg1;

    // 信息2
    @Column(name = "msg2")
    private String msg2;

    // 信息3
    @Column(name = "msg3")
    private String msg3;

    // 信息4
    @Column(name = "msg4")
    private String msg4;

    // 信息5
    @Column(name = "msg5")
    private String msg5;

    // 信息6
    @Column(name = "msg6")
    private String msg6;

    // 信息7
    @Column(name = "msg7")
    private String msg7;

    // 信息8
    @Column(name = "msg8")
    private String msg8;

    // 信息9
    @Column(name = "msg9")
    private String msg9;

    // 视频监控链接
    @Column(name = "videoadr")
    private String videoadr;

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

    public void copy(SProject source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}