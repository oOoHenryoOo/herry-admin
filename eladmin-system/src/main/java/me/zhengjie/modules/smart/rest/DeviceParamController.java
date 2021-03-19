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
import me.zhengjie.modules.smart.domain.DeviceParam;
import me.zhengjie.modules.smart.service.DeviceParamService;
import me.zhengjie.modules.smart.service.dto.DeviceParamQueryCriteria;
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
@Api(tags = "设备参数管理")
@RequestMapping("/api/deviceParam")
public class DeviceParamController {

    private final DeviceParamService deviceParamService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('deviceParam:list')")
    public void download(HttpServletResponse response, DeviceParamQueryCriteria criteria) throws IOException {
        deviceParamService.download(deviceParamService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询设备参数")
    @ApiOperation("查询设备参数")
    @PreAuthorize("@el.check('deviceParam:list')")
    public ResponseEntity<Object> query(DeviceParamQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(deviceParamService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增设备参数")
    @ApiOperation("新增设备参数")
    @PreAuthorize("@el.check('deviceParam:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DeviceParam resources){
        return new ResponseEntity<>(deviceParamService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改设备参数")
    @ApiOperation("修改设备参数")
    @PreAuthorize("@el.check('deviceParam:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DeviceParam resources){
        deviceParamService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除设备参数")
    @ApiOperation("删除设备参数")
    @PreAuthorize("@el.check('deviceParam:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        deviceParamService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}