package me.zhengjie.modules.smart.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @author lhl
* @date 2019-11-14
*/
@Entity
@Data
@Table(name="s_crop")
public class SCrop implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 关联农场Id
    @Column(name = "projectid")
    private String projectid;

    // 农作物名称
    @Column(name = "scropnmae")
    private String scropnmae;

    // 种植面积(亩)
    @Column(name = "area")
    private BigDecimal area;

    // 种植日期
    @Column(name = "sowtime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    private Timestamp sowtime;

    // 生长周期
    @Column(name = "cycle")
    private String cycle;

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SCrop source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}