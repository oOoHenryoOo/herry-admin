package me.zhengjie.modules.smart.rest;

import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.smart.domain.SProjectDevice;
import me.zhengjie.modules.smart.service.SProjectDeviceService;
import me.zhengjie.modules.smart.service.dto.SDeviceDTO;
import me.zhengjie.modules.smart.service.dto.SProjectDeviceQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.util.List;

/**
* @author lhl
* @date 2019-10-23
*/
@Api(tags = "SProjectDevice管理")
@RestController
@RequestMapping("api")
public class SProjectDeviceController {

    @Autowired
    private SProjectDeviceService sProjectDeviceService;

    @Log("查询SProjectDevice")
    @ApiOperation(value = "查询SProjectDevice")
    @GetMapping(value = "/sProjectDevice")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTDEVICE_ALL','SPROJECTDEVICE_SELECT')")
    public ResponseEntity getSProjectDevices(SProjectDeviceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sProjectDeviceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SProjectDevice")
    @ApiOperation(value = "新增SProjectDevice")
    @PostMapping(value = "/sProjectDevice")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTDEVICE_ALL','SPROJECTDEVICE_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SProjectDevice resources){
        return new ResponseEntity(sProjectDeviceService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SProjectDevice")
    @ApiOperation(value = "修改SProjectDevice")
    @PutMapping(value = "/sProjectDevice")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTDEVICE_ALL','SPROJECTDEVICE_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SProjectDevice resources){
        sProjectDeviceService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SProjectDevice")
    @ApiOperation(value = "删除SProjectDevice")
    @DeleteMapping(value = "/sProjectDevice/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTDEVICE_ALL','SPROJECTDEVICE_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sProjectDeviceService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("删除deleteByProjectidAndDeviceid")
    @ApiOperation(value = "删除deleteByProjectidAndDeviceid")
    @PostMapping(value = "/deleteByProjectidAndDeviceid")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTDEVICE_ALL','SPROJECTDEVICE_DELETE')")
    public ResponseEntity deleteByProjectidAndDeviceid(@Validated @RequestBody SProjectDeviceQueryCriteria criteria){
        String projectid = criteria.getProjectid();
        String deviceid = criteria.getDeviceid();
        sProjectDeviceService.deleteByProjectidAndDeviceid(projectid, deviceid);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("获取用户项目下的设备")
    @ApiOperation(value = "获取用户项目下的设备")
    @GetMapping(value = "/getDeviceByProjectAndType")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTDEVICE_ALL','SPROJECTDEVICE_DELETE')")
    public ResponseEntity getDeviceByProjectAndType(@RequestParam(value = "projectId") String projectId,
                                                    @RequestParam(value = "type", required=false) String type){

        List<SDeviceDTO> list = sProjectDeviceService.getDeviceByProjectAndType(projectId, type);
        return new ResponseEntity(list, HttpStatus.OK);
    }
}