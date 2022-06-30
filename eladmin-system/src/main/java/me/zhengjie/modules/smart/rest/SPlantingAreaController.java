package me.zhengjie.modules.smart.rest;

import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.smart.domain.SPlantingArea;
import me.zhengjie.modules.smart.service.SPlantingAreaService;
import me.zhengjie.modules.smart.service.dto.SPlantingAreaQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

/**
* @author liujing
* @date 2019-11-22
*/
@Api(tags = "SPlantingArea管理")
@RestController
@RequestMapping("api")
public class SPlantingAreaController {

    @Autowired
    private SPlantingAreaService sPlantingAreaService;

    @Log("查询SPlantingArea")
    @ApiOperation(value = "查询SPlantingArea")
    @GetMapping(value = "/sPlantingArea")
    @PreAuthorize("hasAnyRole('ADMIN','SPLANTINGAREA_ALL','SPLANTINGAREA_SELECT')")
    public ResponseEntity getSPlantingAreas(SPlantingAreaQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sPlantingAreaService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SPlantingArea")
    @ApiOperation(value = "新增SPlantingArea")
    @PostMapping(value = "/sPlantingArea")
    @PreAuthorize("hasAnyRole('ADMIN','SPLANTINGAREA_ALL','SPLANTINGAREA_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SPlantingArea resources){
        return new ResponseEntity(sPlantingAreaService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SPlantingArea")
    @ApiOperation(value = "修改SPlantingArea")
    @PutMapping(value = "/sPlantingArea")
    @PreAuthorize("hasAnyRole('ADMIN','SPLANTINGAREA_ALL','SPLANTINGAREA_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SPlantingArea resources){
        sPlantingAreaService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SPlantingArea")
    @ApiOperation(value = "删除SPlantingArea")
    @DeleteMapping(value = "/sPlantingArea/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SPLANTINGAREA_ALL','SPLANTINGAREA_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sPlantingAreaService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}