package me.zhengjie.modules.smart.rest;

import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.smart.domain.SDeviceLog;
import me.zhengjie.modules.smart.domain.SDeviceWay;
import me.zhengjie.modules.smart.service.SDeviceLogService;
import me.zhengjie.modules.smart.service.dto.SDeviceLogQueryCriteria;
import me.zhengjie.modules.smart.service.dto.SDeviceWayDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.util.List;
import java.util.Map;

/**
* @author lhl
* @date 2019-10-30
*/
@Api(tags = "SDeviceLog管理")
@RestController
@RequestMapping("api")
public class SDeviceLogController {

    @Autowired
    private SDeviceLogService sDeviceLogService;

    @Log("查询SDeviceLog")
    @ApiOperation(value = "查询SDeviceLog")
    @GetMapping(value = "/sDeviceLog")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICELOG_ALL','SDEVICELOG_SELECT')")
    public ResponseEntity getSDeviceLogs(SDeviceLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sDeviceLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SDeviceLog")
    @ApiOperation(value = "新增SDeviceLog")
    @PostMapping(value = "/sDeviceLog")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICELOG_ALL','SDEVICELOG_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SDeviceLog resources){
        return new ResponseEntity(sDeviceLogService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SDeviceLog")
    @ApiOperation(value = "修改SDeviceLog")
    @PutMapping(value = "/sDeviceLog")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICELOG_ALL','SDEVICELOG_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SDeviceLog resources){
        sDeviceLogService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SDeviceLog")
    @ApiOperation(value = "删除SDeviceLog")
    @DeleteMapping(value = "/sDeviceLog/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICELOG_ALL','SDEVICELOG_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sDeviceLogService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("loadLogByDeviceid")
    @ApiOperation(value = "loadLogByDeviceid")
    @PostMapping(value = "/loadLogByDeviceid")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICEWAY_ALL','SDEVICEWAY_EDIT')")
    public ResponseEntity loadLogByDeviceid(@Validated @RequestBody SDeviceWay resources, Pageable pageable){
        String deviceid = resources.getDeviceid();
        Map<String, Object> list = sDeviceLogService.findByDeviceid(deviceid, pageable);
        return new ResponseEntity(list,HttpStatus.OK);
    }
}