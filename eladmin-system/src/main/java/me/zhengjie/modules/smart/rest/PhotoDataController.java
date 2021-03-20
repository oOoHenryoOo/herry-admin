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
import me.zhengjie.modules.smart.domain.PhotoData;
import me.zhengjie.modules.smart.service.PhotoDataService;
import me.zhengjie.modules.smart.service.dto.PhotoDataQueryCriteria;
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
@Api(tags = "图片识别数据信息管理")
@RequestMapping("/api/photoData")
public class PhotoDataController {

    private final PhotoDataService photoDataService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('photoData:list')")
    public void download(HttpServletResponse response, PhotoDataQueryCriteria criteria) throws IOException {
        photoDataService.download(photoDataService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询图片识别数据信息")
    @ApiOperation("查询图片识别数据信息")
    @PreAuthorize("@el.check('photoData:list')")
    public ResponseEntity<Object> query(PhotoDataQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(photoDataService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增图片识别数据信息")
    @ApiOperation("新增图片识别数据信息")
    @PreAuthorize("@el.check('photoData:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PhotoData resources){
        return new ResponseEntity<>(photoDataService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改图片识别数据信息")
    @ApiOperation("修改图片识别数据信息")
    @PreAuthorize("@el.check('photoData:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PhotoData resources){
        photoDataService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除图片识别数据信息")
    @ApiOperation("删除图片识别数据信息")
    @PreAuthorize("@el.check('photoData:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        photoDataService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}