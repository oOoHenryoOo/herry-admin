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
import me.zhengjie.modules.smart.domain.Device;
import me.zhengjie.modules.smart.service.DeviceService;
import me.zhengjie.modules.smart.service.dto.DeviceQueryCriteria;
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
* @date 2021-04-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "设备管理")
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('device:list')")
    public void download(HttpServletResponse response, DeviceQueryCriteria criteria) throws IOException {
        deviceService.download(deviceService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询设备")
    @ApiOperation("查询设备")
    @PreAuthorize("@el.check('device:list')")
    public ResponseEntity<Object> query(DeviceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(deviceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增设备")
    @ApiOperation("新增设备")
    @PreAuthorize("@el.check('device:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Device resources){
        return new ResponseEntity<>(deviceService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改设备")
    @ApiOperation("修改设备")
    @PreAuthorize("@el.check('device:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Device resources){
        deviceService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除设备")
    @ApiOperation("删除设备")
    @PreAuthorize("@el.check('device:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        deviceService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}