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
* @date 2021-04-28
**/
@Entity
@Data
@Table(name="p_device")
public class Device implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "主键")
    private Long id;

    @Column(name = "company")
    @ApiModelProperty(value = "所属公司")
    private String company;

    @Column(name = "imei")
    @ApiModelProperty(value = "IMEI码")
    private String imei;

    @Column(name = "deviceid")
    @ApiModelProperty(value = "设备id")
    private String deviceid;

    @Column(name = "devicetype")
    @ApiModelProperty(value = "监测设备类型")
    private String devicetype;

    @Column(name = "name")
    @ApiModelProperty(value = "设备名称")
    private String name;

    @Column(name = "nt")
    @ApiModelProperty(value = "描述")
    private String nt;

    @Column(name = "type")
    @ApiModelProperty(value = "设备类型 0:nb 1:4g")
    private String type;

    @Column(name = "address")
    @ApiModelProperty(value = "安装位置")
    private String address;

    @Column(name = "jd")
    @ApiModelProperty(value = "经度")
    private Double jd;

    @Column(name = "wd")
    @ApiModelProperty(value = "纬度")
    private Double wd;

    @Column(name = "timetrack")
    @ApiModelProperty(value = "采集频率")
    private Integer timetrack;

    @Column(name = "timetrack2")
    @ApiModelProperty(value = "上传频率")
    private Integer timetrack2;

    @Column(name = "valid")
    @ApiModelProperty(value = "是否有效")
    private String valid;

    @Column(name = "ip")
    @ApiModelProperty(value = "ip")
    private String ip;

    @Column(name = "port")
    @ApiModelProperty(value = "端口")
    private String port;

    @Column(name = "qrcode")
    @ApiModelProperty(value = "二维码")
    private String qrcode;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;

    @Column(name = "workmode")
    @ApiModelProperty(value = "工作模式 0手动1自动")
    private String workmode;

    @Column(name = "onlinetime")
    @ApiModelProperty(value = "在线时间")
    private Timestamp onlinetime;

    @Column(name = "online")
    @ApiModelProperty(value = "在线")
    private String online;

    @Column(name = "expire")
    @ApiModelProperty(value = "到期时间")
    private Timestamp expire;

    @Column(name = "isp")
    @ApiModelProperty(value = "运营商")
    private String isp;

    public void copy(Device source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}