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
* @date 2021-02-26
**/
@Data
public class DeviceParamDto implements Serializable {

    /** 主键 */
    private Long id;

    /** 传感器id 唯一设备号 */
    private String deviceid;

    /** 模式0:自动模式1:手动模式 */
    private Integer dmode;

    /** 设定手动模式下的开关状态 */
    private Integer dstatus;

    /** 开灯时间 */
    private String opentime;

    /** 关灯时间 */
    private String closetime;

    /** 拍照间隔 */
    private String photointerval;

    /** 烘干时间 */
    private String drytime;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 备注 */
    private String nt;
}