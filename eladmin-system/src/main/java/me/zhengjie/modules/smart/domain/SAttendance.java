package me.zhengjie.modules.smart.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @author lhl
* @date 2019-11-14
*/
@Entity
@Data
@Table(name="s_attendance")
public class SAttendance implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 关联农场Id
    @Column(name = "projectid")
    private String projectid;

    // 出勤人数
    @Column(name = "attendance")
    private Integer attendance;

    // 出勤日期
    @Column(name = "attTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    private Timestamp atttime;

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SAttendance source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}