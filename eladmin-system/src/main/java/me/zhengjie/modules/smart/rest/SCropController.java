package me.zhengjie.modules.smart.rest;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import me.zhengjie.aop.log.Log;
import me.zhengjie.modules.smart.domain.SCrop;
import me.zhengjie.modules.smart.service.SCropService;
import me.zhengjie.modules.smart.service.SPlantingAreaService;
import me.zhengjie.modules.smart.service.SProjectService;
import me.zhengjie.modules.smart.service.dto.SCropDTO;
import me.zhengjie.modules.smart.service.dto.SCropQueryCriteria;
import me.zhengjie.modules.smart.service.dto.SPlantingAreaDTO;
import me.zhengjie.modules.smart.service.dto.SProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author lhl
* @date 2019-11-14
*/
@Api(tags = "SCrop管理")
@RestController
@RequestMapping("api")
public class SCropController {

    @Autowired
    private SCropService sCropService;

    @Autowired
    private SPlantingAreaService sPlantingAreaService;

    @Autowired
    private SProjectService sProjectService;

    @Log("查询SCrop")
    @ApiOperation(value = "查询SCrop")
    @GetMapping(value = "/sCrop")
    @PreAuthorize("hasAnyRole('ADMIN','SCROP_ALL','SCROP_SELECT')")
    public ResponseEntity getSCrops(SCropQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sCropService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询SCropNoPage")
    @ApiOperation(value = "查询SCropNoPage")
    @GetMapping(value = "/getSCropsNoPage")
    public ResponseEntity getSCropsNoPage(){
        SCropQueryCriteria criteria = new SCropQueryCriteria();
        return new ResponseEntity(sCropService.queryAll(criteria),HttpStatus.OK);
    }

    @Log("getSCropsNoPageByproid")
    @ApiOperation(value = "getSCropsNoPageByproid")
    @GetMapping(value = "/getSCropsNoPageByproid/{projectid}")
    public ResponseEntity getSCropsNoPageByproid(@PathVariable Long projectid){
        List<SCropDTO> list = sCropService.findByProjectid(projectid.toString());
        SProjectDTO sProjectDTO = sProjectService.findById(projectid);
        for(int i=list.size()-1;i>=0;i--){
            if(sProjectDTO != null){
                list.get(i).setProjectid(sProjectDTO.getProname());
            }
            long day = DateUtil.between(new Date(),list.get(i).getSowtime(), DateUnit.DAY);
            if(day<365){
                list.get(i).setNt(day+"");
            }else{
                list.remove(i);
            }
        }
        return new ResponseEntity(list,HttpStatus.OK);
    }

    @Log("新增SCrop")
    @ApiOperation(value = "新增SCrop")
    @PostMapping(value = "/sCrop")
    @PreAuthorize("hasAnyRole('ADMIN','SCROP_ALL','SCROP_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SCrop resources){
        return new ResponseEntity(sCropService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SCrop")
    @ApiOperation(value = "修改SCrop")
    @PutMapping(value = "/sCrop")
    @PreAuthorize("hasAnyRole('ADMIN','SCROP_ALL','SCROP_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SCrop resources){
        sCropService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SCrop")
    @ApiOperation(value = "删除SCrop")
    @DeleteMapping(value = "/sCrop/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SCROP_ALL','SCROP_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sCropService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("getAreachartdata")
    @ApiOperation(value = "getAreachartdata")
    @RequestMapping(value = "/getAreachartdata/{projectid}")
    public ResponseEntity getAreachartdata(@PathVariable Long projectid){
        List<SCropDTO> list = sCropService.findByProjectid(projectid.toString());
        List<SPlantingAreaDTO> list1 = sPlantingAreaService.findByProjectid(projectid.toString());
        int[] timeArr = new int[list1.size()];
        BigDecimal[] areaArr = new BigDecimal[list1.size()];
        BigDecimal[] cropareaArr = new BigDecimal[list1.size()];
        for(int i=0;i<list1.size();i++){
            java.sql.Timestamp ts = list1.get(i).getCreateTime();
            java.util.Date date = new java.util.Date(ts.getTime());
            int ct = date.getYear()+1900;
            timeArr[i] = ct;
            BigDecimal area = list1.get(i).getArea();
            areaArr[i] = area;
            BigDecimal sum = BigDecimal.valueOf(0);
           for(int j=0;j<list.size();j++){
               java.sql.Timestamp ts1 = list.get(j).getSowtime();
               java.util.Date date1 = new java.util.Date(ts1.getTime());
               int st = date1.getYear()+1900;
               if(ct==st){
                   BigDecimal croparea = list.get(j).getArea();
                   sum = sum.add(croparea);
               }
           }
            cropareaArr[i] = sum;
        }
//        sCropService.delete(projectid);
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("timeArr",timeArr);
        dataMap.put("areaArr",areaArr);
        dataMap.put("cropareaArr",cropareaArr);
        return new ResponseEntity(dataMap,HttpStatus.OK);
//        return new ResponseEntity(HttpStatus.OK);
    }
}