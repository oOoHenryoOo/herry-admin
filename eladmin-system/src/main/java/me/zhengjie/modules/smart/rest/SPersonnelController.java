package me.zhengjie.modules.smart.rest;

import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.smart.domain.SPersonnel;
import me.zhengjie.modules.smart.service.SPersonnelService;
import me.zhengjie.modules.smart.service.dto.SPersonnelQueryCriteria;
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
* @date 2019-11-14
*/
@Api(tags = "SPersonnel管理")
@RestController
@RequestMapping("api")
public class SPersonnelController {

    @Autowired
    private SPersonnelService sPersonnelService;

    @Log("查询SPersonnel")
    @ApiOperation(value = "查询SPersonnel")
    @GetMapping(value = "/sPersonnel")
    @PreAuthorize("hasAnyRole('ADMIN','SPERSONNEL_ALL','SPERSONNEL_SELECT')")
    public ResponseEntity getSPersonnels(SPersonnelQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sPersonnelService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SPersonnel")
    @ApiOperation(value = "新增SPersonnel")
    @PostMapping(value = "/sPersonnel")
    @PreAuthorize("hasAnyRole('ADMIN','SPERSONNEL_ALL','SPERSONNEL_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SPersonnel resources){
        return new ResponseEntity(sPersonnelService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SPersonnel")
    @ApiOperation(value = "修改SPersonnel")
    @PutMapping(value = "/sPersonnel")
    @PreAuthorize("hasAnyRole('ADMIN','SPERSONNEL_ALL','SPERSONNEL_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SPersonnel resources){
        sPersonnelService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SPersonnel")
    @ApiOperation(value = "删除SPersonnel")
    @DeleteMapping(value = "/sPersonnel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SPERSONNEL_ALL','SPERSONNEL_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sPersonnelService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}