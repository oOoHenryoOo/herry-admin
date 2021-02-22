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
package me.zhengjie.modules.smart.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.smart.domain.DevicePhoto;
import me.zhengjie.modules.smart.service.DevicePhotoService;
import me.zhengjie.modules.smart.service.dto.DevicePhotoQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author lhl
* @date 2021-02-20
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "设备图片表管理")
@RequestMapping("/api/devicePhoto")
public class DevicePhotoController {

    private final DevicePhotoService devicePhotoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('devicePhoto:list')")
    public void download(HttpServletResponse response, DevicePhotoQueryCriteria criteria) throws IOException {
        devicePhotoService.download(devicePhotoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询设备图片表")
    @ApiOperation("查询设备图片表")
    @PreAuthorize("@el.check('devicePhoto:list')")
    public ResponseEntity<Object> query(DevicePhotoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(devicePhotoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增设备图片表")
    @ApiOperation("新增设备图片表")
    @PreAuthorize("@el.check('devicePhoto:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DevicePhoto resources){
        return new ResponseEntity<>(devicePhotoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改设备图片表")
    @ApiOperation("修改设备图片表")
    @PreAuthorize("@el.check('devicePhoto:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DevicePhoto resources){
        devicePhotoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除设备图片表")
    @ApiOperation("删除设备图片表")
    @PreAuthorize("@el.check('devicePhoto:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        devicePhotoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}