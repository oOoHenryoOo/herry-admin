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
* @date 2021-02-20
**/
@Data
public class DevicePhotoDto implements Serializable {

    /** ID */
    private Long id;

    /** 传感器id */
    private String deviceid;

    /** 文件真实的名称 */
    private String realName;

    /** 文件名 */
    private String name;

    /** 后缀 */
    private String suffix;

    /** 路径 */
    private String path;

    /** 类型 */
    private String type;

    /** 大小 */
    private String size;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 创建日期 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;
}