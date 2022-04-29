package me.zhengjie.modules.smart.rest;

import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.smart.domain.SDevicetype;
import me.zhengjie.modules.smart.service.SDevicetypeService;
import me.zhengjie.modules.smart.service.dto.SDevicetypeQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

/**
* @author lhl
* @date 2019-10-21
*/
@Api(tags = "SDevicetype管理")
@RestController
@RequestMapping("api")
public class SDevicetypeController {

    @Autowired
    private SDevicetypeService sDevicetypeService;

    @Log("查询SDevicetype")
    @ApiOperation(value = "查询SDevicetype")
    @GetMapping(value = "/sDevicetype")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICETYPE_ALL','SDEVICETYPE_SELECT')")
    public ResponseEntity getSDevicetypes(SDevicetypeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sDevicetypeService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询devicetype不分页")
    @ApiOperation(value = "查询devicetype不分页")
    @PostMapping(value = "/getDevicetypeNopage")
    public ResponseEntity getDevicetypeNopage(){
        SDevicetypeQueryCriteria criteria = new SDevicetypeQueryCriteria();
        return new ResponseEntity(sDevicetypeService.queryAll(criteria),HttpStatus.OK);
    }

    @Log("新增SDevicetype")
    @ApiOperation(value = "新增SDevicetype")
    @PostMapping(value = "/sDevicetype")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICETYPE_ALL','SDEVICETYPE_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SDevicetype resources){
        return new ResponseEntity(sDevicetypeService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SDevicetype")
    @ApiOperation(value = "修改SDevicetype")
    @PutMapping(value = "/sDevicetype")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICETYPE_ALL','SDEVICETYPE_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SDevicetype resources){
        sDevicetypeService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SDevicetype")
    @ApiOperation(value = "删除SDevicetype")
    @DeleteMapping(value = "/sDevicetype/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICETYPE_ALL','SDEVICETYPE_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sDevicetypeService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}