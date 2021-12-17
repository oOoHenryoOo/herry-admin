package me.zhengjie.modules.smart.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import java.io.Serializable;

/**
* @author lhl
* @date 2019-11-14
*/
@Entity
@Data
@Table(name="s_personnel")
public class SPersonnel implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 关联农场Id
    @Column(name = "projectid")
    private String projectid;

    // 人员结构名称
    @Column(name = "name")
    private String name;

    // 男人人数
    @Column(name = "man")
    private Integer man;

    // 女人人数
    @Column(name = "woman")
    private Integer woman;

    // 孤寡老人人数
    @Column(name = "oldman")
    private Integer oldman;

    // 留守儿童人数
    @Column(name = "leftoverhildren")
    private Integer leftoverhildren;

    // 贫困人口
    @Column(name = "poor")
    private Integer poor;

    // 残疾人口
    @Column(name = "disabled")
    private Integer disabled;

    // 35岁一下人数
    @Column(name = "thirtyfivedown")
    private Integer thirtyfivedown;

    // 35-45岁人数
    @Column(name = "fortyfive")
    private Integer fortyfive;

    // 45-55岁人数
    @Column(name = "fiftyfive")
    private Integer fiftyfive;

    // 大于55岁人数
    @Column(name = "fiftyfiveup")
    private Integer fiftyfiveup;

    // 备注
    @Column(name = "nt")
    private String nt;

    public void copy(SPersonnel source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}