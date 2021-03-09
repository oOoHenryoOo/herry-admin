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
@Table(name="p_photo_data")
public class PhotoData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "主键")
    private Long id;

    @Column(name = "deviceid")
    @ApiModelProperty(value = "设备ID")
    private String deviceid;

    @Column(name = "photorealname")
    @ApiModelProperty(value = "图片名称")
    private String photorealname;

    @Column(name = "pestid")
    @ApiModelProperty(value = "害虫代码")
    private String pestid;

    @Column(name = "pestname")
    @ApiModelProperty(value = "害虫名称")
    private String pestname;

    @Column(name = "pestcount")
    @ApiModelProperty(value = "害虫数量")
    private Integer pestcount;

    @Column(name = "pictime")
    @ApiModelProperty(value = "图片时间")
    private Timestamp pictime;

    @Column(name = "datatime")
    @ApiModelProperty(value = "识别时间")
    private Timestamp datatime;

    @Column(name = "nt")
    @ApiModelProperty(value = "备注")
    private String nt;

    public void copy(PhotoData source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}