package me.zhengjie.modules.smart.rest;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.aop.log.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.security.JwtUser;
import me.zhengjie.modules.smart.domain.SProject;
import me.zhengjie.modules.smart.domain.SProjectUser;
import me.zhengjie.modules.smart.service.SDeviceService;
import me.zhengjie.modules.smart.service.SProjectDeviceService;
import me.zhengjie.modules.smart.service.SProjectService;
import me.zhengjie.modules.smart.service.SProjectUserService;
import me.zhengjie.modules.smart.service.dto.SDeviceDTO;
import me.zhengjie.modules.smart.service.dto.SProjectDTO;
import me.zhengjie.modules.smart.service.dto.SProjectQueryCriteria;
import me.zhengjie.modules.smart.service.dto.SProjectUserDTO;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.util.List;
import java.util.Map;

/**
* @author lhl
* @date 2019-10-22
*/
@Slf4j
@Api(tags = "SProject管理")
@RestController
@RequestMapping("api")
public class SProjectController {

    @Autowired
    private SProjectService sProjectService;

    @Autowired
    private SProjectUserService sProjectUserService;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private SDeviceService sDeviceService;

    @Log("查询SProject")
    @ApiOperation(value = "查询SProject")
    @GetMapping(value = "/sProject")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECT_ALL','SPROJECT_SELECT')")
    public ResponseEntity getSProjects(SProjectQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sProjectService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询getSProjectsByUserid")
    @ApiOperation(value = "查询getSProjectsByUserid")
    @GetMapping(value = "/getSProjectsByUserid")
//    @PreAuthorize("hasAnyRole('ADMIN','SPROJECT_ALL','SPROJECT_SELECT')")
    public ResponseEntity getSProjectsByUserid(SProjectQueryCriteria criteria, Pageable pageable){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        Map<String,Object> map = sProjectService.getSProjectsByUserid(userid, criteria,pageable);
        return new ResponseEntity(map,HttpStatus.OK);
    }

    @Log("查询SProject不分页")
    @ApiOperation(value = "查询SProject不分页")
    @PostMapping(value = "/getSProjectsNopage")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECT_ALL','SPROJECT_SELECT')")
    public ResponseEntity getSProjectsNopage(){
        SProjectQueryCriteria criteria = new SProjectQueryCriteria();
        return new ResponseEntity(sProjectService.queryAll(criteria),HttpStatus.OK);
    }

    @Log("新增SProject")
    @ApiOperation(value = "新增SProject")
    @PostMapping(value = "/sProject")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECT_ALL','SPROJECT_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SProject resources){
        SProjectDTO sProjectDTO = sProjectService.create(resources);
        //增加用户项目关联数据
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        SProjectUser sProjectUser = new SProjectUser();
        sProjectUser.setProjectid(sProjectDTO.getId().toString());
        sProjectUser.setUserid(jwtUser.getId().toString());
        sProjectUser.setState("1");
        sProjectUser.setValid("1");
        sProjectUserService.create(sProjectUser);
        log.debug("设备绑定成功!successful!"+jwtUser.getId().toString()+" "+jwtUser.getId().toString());
        return new ResponseEntity(sProjectDTO,HttpStatus.CREATED);
    }

    @Log("修改SProject")
    @ApiOperation(value = "修改SProject")
    @PutMapping(value = "/sProject")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECT_ALL','SPROJECT_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SProject resources){
        sProjectService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SProject")
    @ApiOperation(value = "删除SProject")
    @DeleteMapping(value = "/sProject/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECT_ALL','SPROJECT_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        //1、查询这个项目是否还有绑定的设备和控制器
        List<SDeviceDTO> list = sDeviceService.getDeviceByProjectid(id);
        //2、如果有 (提示还绑定XXX设备(不提示了))，删除项目用户绑定关系
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        if(list.size()>0){
            SProjectUserDTO sProjectUserDTO = sProjectUserService.findByProjectidAndUserid(id.toString(),userid);
            if(sProjectUserDTO != null){
                log.info("delete删除项目用户绑定关系！1  "+sProjectUserDTO.getId());
                sProjectUserService.delete(sProjectUserDTO.getId());
            }
//            log.info("delete当前项目还有与之绑定的设备，请先删除相应设备！");
//            throw new BadRequestException("当前项目还有与之绑定的设备，请先删除相应设备！");
        }else{
            //3、如果没有绑定的设备，删除项目，删除项目用户绑定关系
            log.info("delete删除项目！"+id);
            sProjectService.delete(id);
            SProjectUserDTO sProjectUserDTO = sProjectUserService.findByProjectidAndUserid(id.toString(),userid);
            if(sProjectUserDTO != null){
                log.info("delete删除项目用户绑定关系！2  "+sProjectUserDTO.getId());
                sProjectUserService.delete(sProjectUserDTO.getId());
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}