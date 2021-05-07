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
package me.zhengjie.modules.smart.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author lhl
* @date 2021-04-28
**/
@Data
public class DeviceDto implements Serializable {

    /** 主键 */
    private Long id;

    /** 所属公司 */
    private String company;

    /** IMEI码 */
    private String imei;

    /** 设备id */
    private String deviceid;

    /** 监测设备类型 */
    private String devicetype;

    /** 设备名称 */
    private String name;

    /** 描述 */
    private String nt;

    /** 设备类型 0:nb 1:4g */
    private String type;

    /** 安装位置 */
    private String address;

    /** 经度 */
    private Double jd;

    /** 纬度 */
    private Double wd;

    /** 采集频率 */
    private Integer timetrack;

    /** 上传频率 */
    private Integer timetrack2;

    /** 是否有效 */
    private String valid;

    /** ip */
    private String ip;

    /** 端口 */
    private String port;

    /** 二维码 */
    private String qrcode;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 工作模式 0手动1自动 */
    private String workmode;

    /** 在线时间 */
    private Timestamp onlinetime;

    /** 在线 */
    private String online;

    /** 到期时间 */
    private Timestamp expire;

    /** 运营商 */
    private String isp;
}