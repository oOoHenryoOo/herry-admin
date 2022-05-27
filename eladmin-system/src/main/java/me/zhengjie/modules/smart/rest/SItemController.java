package me.zhengjie.modules.smart.rest;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.smart.domain.SItem;
import me.zhengjie.modules.smart.service.SItemService;
import me.zhengjie.modules.smart.service.dto.SItemQueryCriteria;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

/**
* @author lhl
* @date 2019-10-21
*/
@Slf4j
@Api(tags = "SItem管理")
@RestController
@RequestMapping("api")
public class SItemController {

    @Autowired
    private SItemService sItemService;

    @Log("查询SItem")
    @ApiOperation(value = "查询SItem")
    @GetMapping(value = "/sItem")
    @PreAuthorize("hasAnyRole('ADMIN','SITEM_ALL','SITEM_SELECT')")
    public ResponseEntity getSItems(SItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SItem")
    @ApiOperation(value = "新增SItem")
    @PostMapping(value = "/sItem")
    @PreAuthorize("hasAnyRole('ADMIN','SITEM_ALL','SITEM_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SItem resources){
        resources.setValid("1");
        return new ResponseEntity(sItemService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SItem")
    @ApiOperation(value = "修改SItem")
    @PutMapping(value = "/sItem")
    @PreAuthorize("hasAnyRole('ADMIN','SITEM_ALL','SITEM_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SItem resources){
        sItemService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SItem")
    @ApiOperation(value = "删除SItem")
    @DeleteMapping(value = "/sItem/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SITEM_ALL','SITEM_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sItemService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("查询全部SItem")
    @ApiOperation(value = "查询全部SItem")
    @GetMapping(value = "/sAllItem")
    @PreAuthorize("hasAnyRole('ADMIN','SITEM_ALL','SITEM_SELECT')")
    public ResponseEntity getAllSItems(SItemQueryCriteria criteria){
        return new ResponseEntity(sItemService.queryAll(criteria),HttpStatus.OK);
    }

}
