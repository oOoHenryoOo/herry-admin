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
@Table(name="s_project_user")
public class SProjectUser implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 项目ID
    @Column(name = "projectid")
    private String projectid;

    // 用户ID
    @Column(name = "userid")
    private String userid;

    // 设备状态
    @Column(name = "state")
    private String state;

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

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SProjectUser source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}