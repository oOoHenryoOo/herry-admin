package me.zhengjie.modules.smart.rest;

import cn.hutool.core.date.DateUtil;
import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.smart.domain.SAttendance;
import me.zhengjie.modules.smart.service.SAttendanceService;
import me.zhengjie.modules.smart.service.dto.SAttendanceDTO;
import me.zhengjie.modules.smart.service.dto.SAttendanceQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.util.Date;
import java.util.List;

/**
* @author lhl
* @date 2019-11-14
*/
@Api(tags = "SAttendance管理")
@RestController
@RequestMapping("api")
public class SAttendanceController {

    @Autowired
    private SAttendanceService sAttendanceService;

    @Log("查询SAttendance")
    @ApiOperation(value = "查询SAttendance")
    @GetMapping(value = "/sAttendance")
    @PreAuthorize("hasAnyRole('ADMIN','SATTENDANCE_ALL','SATTENDANCE_SELECT')")
    public ResponseEntity getSAttendances(SAttendanceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sAttendanceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SAttendance")
    @ApiOperation(value = "新增SAttendance")
    @PostMapping(value = "/sAttendance")
    @PreAuthorize("hasAnyRole('ADMIN','SATTENDANCE_ALL','SATTENDANCE_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SAttendance resources){
        return new ResponseEntity(sAttendanceService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SAttendance")
    @ApiOperation(value = "修改SAttendance")
    @PutMapping(value = "/sAttendance")
    @PreAuthorize("hasAnyRole('ADMIN','SATTENDANCE_ALL','SATTENDANCE_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SAttendance resources){
        sAttendanceService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SAttendance")
    @ApiOperation(value = "删除SAttendance")
    @DeleteMapping(value = "/sAttendance/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SATTENDANCE_ALL','SATTENDANCE_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sAttendanceService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("本月考勤")
    @ApiOperation(value = "本月考勤")
    @GetMapping(value = "/queryAttendanceByDate/{projectid}")
    public ResponseEntity queryAttendanceByDate (@PathVariable Long projectid) {
        String startTime = DateUtil.format(DateUtil.beginOfMonth(new Date()), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.format(DateUtil.endOfMonth(new Date()), "yyyy-MM-dd HH:mm:ss");
        List<SAttendanceDTO> list = sAttendanceService.queryAttendanceByDate(startTime,endTime,projectid.toString());
        return new ResponseEntity(list,HttpStatus.OK);
    }

}