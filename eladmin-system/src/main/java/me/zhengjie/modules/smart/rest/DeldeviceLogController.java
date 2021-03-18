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
import me.zhengjie.modules.smart.domain.DeldeviceLog;
import me.zhengjie.modules.smart.service.DeldeviceLogService;
import me.zhengjie.modules.smart.service.dto.DeldeviceLogQueryCriteria;
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
@Api(tags = "设备删除管理")
@RequestMapping("/api/deldeviceLog")
public class DeldeviceLogController {

    private final DeldeviceLogService deldeviceLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('deldeviceLog:list')")
    public void download(HttpServletResponse response, DeldeviceLogQueryCriteria criteria) throws IOException {
        deldeviceLogService.download(deldeviceLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询设备删除")
    @ApiOperation("查询设备删除")
    @PreAuthorize("@el.check('deldeviceLog:list')")
    public ResponseEntity<Object> query(DeldeviceLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(deldeviceLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增设备删除")
    @ApiOperation("新增设备删除")
    @PreAuthorize("@el.check('deldeviceLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DeldeviceLog resources){
        return new ResponseEntity<>(deldeviceLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改设备删除")
    @ApiOperation("修改设备删除")
    @PreAuthorize("@el.check('deldeviceLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DeldeviceLog resources){
        deldeviceLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除设备删除")
    @ApiOperation("删除设备删除")
    @PreAuthorize("@el.check('deldeviceLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        deldeviceLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}