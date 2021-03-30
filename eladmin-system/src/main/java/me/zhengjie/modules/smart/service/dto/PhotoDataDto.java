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
public class PhotoDataDto implements Serializable {

    /** 主键 */
    private Long id;

    /** 设备ID */
    private String deviceid;

    /** 图片名称 */
    private String photorealname;

    /** 害虫代码 */
    private String pestid;

    /** 害虫名称 */
    private String pestname;

    /** 害虫数量 */
    private Integer pestcount;

    /** 图片时间 */
    private Timestamp pictime;

    /** 识别时间 */
    private Timestamp datatime;

    /** 备注 */
    private String nt;
}