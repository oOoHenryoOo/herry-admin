package me.zhengjie.modules.smart.rest;

import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.security.security.JwtUser;
import me.zhengjie.modules.smart.domain.SProjectUser;
import me.zhengjie.modules.smart.service.SProjectService;
import me.zhengjie.modules.smart.service.SProjectUserService;
import me.zhengjie.modules.smart.service.dto.SProjectUserQueryCriteria;
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

/**
* @author lhl
* @date 2019-10-23
*/
@Api(tags = "SProjectUser管理")
@RestController
@RequestMapping("api")
public class SProjectUserController {

    @Autowired
    private SProjectUserService sProjectUserService;

    @Autowired
    private SProjectService sProjectService;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @Log("查询SProjectUser")
    @ApiOperation(value = "查询SProjectUser")
    @GetMapping(value = "/sProjectUser")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTUSER_ALL','SPROJECTUSER_SELECT')")
    public ResponseEntity getSProjectUsers(SProjectUserQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sProjectUserService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SProjectUser")
    @ApiOperation(value = "新增SProjectUser")
    @PostMapping(value = "/sProjectUser")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTUSER_ALL','SPROJECTUSER_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SProjectUser resources){
        return new ResponseEntity(sProjectUserService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SProjectUser")
    @ApiOperation(value = "修改SProjectUser")
    @PutMapping(value = "/sProjectUser")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTUSER_ALL','SPROJECTUSER_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SProjectUser resources){
        sProjectUserService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SProjectUser")
    @ApiOperation(value = "删除SProjectUser")
    @DeleteMapping(value = "/sProjectUser/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SPROJECTUSER_ALL','SPROJECTUSER_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sProjectUserService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    @Log("通过userid查询project")
    @ApiOperation(value = "通过userid查询project")
    @PostMapping(value = "/findProjectByUserid")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_CREATE')")
    public ResponseEntity findProjectByUserid(){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        return new ResponseEntity(sProjectService.findProjectByUserid(jwtUser.getId().toString()),HttpStatus.OK);
    }
}