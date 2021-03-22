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
import me.zhengjie.modules.smart.domain.UserDevice;
import me.zhengjie.modules.smart.service.UserDeviceService;
import me.zhengjie.modules.smart.service.dto.UserDeviceQueryCriteria;
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
* @date 2021-02-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "用户设备关联表管理")
@RequestMapping("/api/userDevice")
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('userDevice:list')")
    public void download(HttpServletResponse response, UserDeviceQueryCriteria criteria) throws IOException {
        userDeviceService.download(userDeviceService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询用户设备关联表")
    @ApiOperation("查询用户设备关联表")
    @PreAuthorize("@el.check('userDevice:list')")
    public ResponseEntity<Object> query(UserDeviceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(userDeviceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增用户设备关联表")
    @ApiOperation("新增用户设备关联表")
    @PreAuthorize("@el.check('userDevice:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody UserDevice resources){
        return new ResponseEntity<>(userDeviceService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改用户设备关联表")
    @ApiOperation("修改用户设备关联表")
    @PreAuthorize("@el.check('userDevice:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody UserDevice resources){
        userDeviceService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除用户设备关联表")
    @ApiOperation("删除用户设备关联表")
    @PreAuthorize("@el.check('userDevice:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        userDeviceService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}