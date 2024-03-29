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
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author lhl
* @date 2021-02-26
**/
@Entity
@Data
@Table(name="p_device_param")
public class DeviceParam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "主键")
    private Long id;

    @Column(name = "deviceid")
    @ApiModelProperty(value = "传感器id 唯一设备号")
    private String deviceid;

    @Column(name = "dmode")
    @ApiModelProperty(value = "模式0:自动模式1:手动模式")
    private Integer dmode;

    @Column(name = "dstatus")
    @ApiModelProperty(value = "设定手动模式下的开关状态")
    private Integer dstatus;

    @Column(name = "opentime")
    @ApiModelProperty(value = "开灯时间")
    private String opentime;

    @Column(name = "closetime")
    @ApiModelProperty(value = "关灯时间")
    private String closetime;

    @Column(name = "photointerval")
    @ApiModelProperty(value = "拍照间隔")
    private String photointerval;

    @Column(name = "drytime")
    @ApiModelProperty(value = "烘干时间")
    private String drytime;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;

    @Column(name = "nt")
    @ApiModelProperty(value = "备注")
    private String nt;

    public void copy(DeviceParam source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}