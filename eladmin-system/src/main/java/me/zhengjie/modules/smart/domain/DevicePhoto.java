/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.modules.smart.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author lhl
* @date 2021-02-20
**/
@Entity
@Data
@Table(name="p_device_photo")
public class DevicePhoto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "deviceid")
    @ApiModelProperty(value = "传感器id")
    private String deviceid;

    @Column(name = "real_name")
    @ApiModelProperty(value = "文件真实的名称")
    private String realName;

    @Column(name = "name")
    @ApiModelProperty(value = "文件名")
    private String name;

    @Column(name = "suffix")
    @ApiModelProperty(value = "后缀")
    private String suffix;

    @Column(name = "path")
    @ApiModelProperty(value = "路径")
    private String path;

    @Column(name = "type")
    @ApiModelProperty(value = "类型")
    private String type;

    @Column(name = "size")
    @ApiModelProperty(value = "大小")
    private String size;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建者")
    private String createBy;

    @Column(name = "update_by")
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建日期")
    @CreationTimestamp
    private Timestamp createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    @UpdateTimestamp
    private Timestamp updateTime;

    public void copy(DevicePhoto source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}