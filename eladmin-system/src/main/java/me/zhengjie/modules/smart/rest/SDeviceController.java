package me.zhengjie.modules.smart.rest;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.aop.log.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.security.JwtUser;
import me.zhengjie.modules.smart.domain.*;
import me.zhengjie.modules.smart.service.*;
import me.zhengjie.modules.smart.service.dto.*;
import me.zhengjie.modules.smart.service.mapper.SDeviceMapper;
import me.zhengjie.modules.smart.service.mapper.SProjectDeviceMapper;
import me.zhengjie.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;

/**
* @author lhl
* @date 2019-10-23
*/
@Slf4j
@Api(tags = "SDevice管理")
@RestController
@RequestMapping("api")
public class SDeviceController {

    @Autowired
    private SDeviceService sDeviceService;

    @Autowired
    private SDeviceMapper sDeviceMapper;

    @Autowired
    private SProjectDeviceMapper sProjectDeviceMapper;

    @Autowired
    private SDevicetypeService sDevicetypeService;

    @Autowired
    private SProjectDeviceService sProjectDeviceService;

    @Autowired
    private SDeviceWayService sDeviceWayService;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private SProjectUserService sProjectUserService;

    @Value("${relay.contains}")
    private String contains;

    @Value("${relay.LedContains}")
    private String LedContains;

    @Log("查询SDevice")
    @ApiOperation(value = "查询SDevice")
    @GetMapping(value = "/sDevice")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_SELECT')")
    public ResponseEntity getSDevices(SDeviceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sDeviceService.queryAll(criteria,pageable),HttpStatus.OK);
    }
    @Log("查询SDeviceByUserid")
    @ApiOperation(value = "查询SDeviceByUserid")
    @GetMapping(value = "/sDeviceByUserid")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_SELECT')")
    public ResponseEntity sDeviceByUserid(SDeviceQueryCriteria criteria, Pageable pageable){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        /*List<SDeviceDTO> sDeviceDTOlist = ;
        List list = PageUtil.toPage(page,size,sDeviceDTOlist);*/
        return new ResponseEntity(sDeviceService.queryAllByUserid(jwtUser.getId().toString(),criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SDevice")
    @ApiOperation(value = "新增SDevice")
    @PostMapping(value = "/sDevice")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SDevice resources){
        return new ResponseEntity(sDeviceService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SDevice")
    @ApiOperation(value = "修改SDevice")
    @PutMapping(value = "/sDevice")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_EDIT')")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity update(@Validated @RequestBody SDevice resources){
        String projectid = resources.getNt();
        String code = resources.getQrcode();
        String[] codeArr = code.split("-");
        String deviceid = codeArr[0];
        SProjectDeviceDTO sProjectDeviceDTO =sProjectDeviceService.findByProjectidAndDeviceid(projectid, deviceid);
        if(sProjectDeviceDTO == null){
            // step1、删除之前的 设备和项目 绑定关系
            sProjectDeviceService.deleteByDeviceid(deviceid);
            // step2、把设备绑定给当前项目
            JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
            SProjectDevice sProjectDevice = new SProjectDevice();
            sProjectDevice.setProjectid(projectid);
            sProjectDevice.setDeviceid(deviceid);
            sProjectDevice.setState("1");
            sProjectDevice.setValid("1");
            sProjectDeviceService.create(sProjectDevice);
            log.info("修改绑定项目！"+projectid+" "+deviceid);
        }
        resources.setNt(null);
        sDeviceService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SDevice")
    @ApiOperation(value = "删除SDevice")
    @DeleteMapping(value = "/sDevice/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_DELETE')")
    public ResponseEntity delete(@PathVariable String id){
        try {
            sDeviceService.delete(id);
        }catch (Exception e) {
            if(e.getMessage().contains("could not execute statement")){
                throw new BadRequestException("删除设备之前请先删除报警信息、智能控制中与本设备相关信息！");
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("绑定设备")
    @ApiOperation(value = "绑定设备")
    @PostMapping(value = "/bindingDevice")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_CREATE')")
    public ResponseEntity bindingDevice(@Validated @RequestBody SDevice resources){
        String projectid = resources.getNt();
        String code = resources.getQrcode();
        String jd = resources.getJd().toString();
        String wd = resources.getWd().toString();
        String name = resources.getName();
        /*1、CPU-IMEI-type设备类型（3位码）
         *2、CPU-IMEI/4G卡号/WiFi名和密码-设备类型-检测类型（4位码）
         *      CPU-IMEI-0-检测类型（NB 4位码）
         *      CPU-4G卡号-1-检测类型（4G 4位码）
         *      CPU-WiFi名和密码-2-检测类型（WiFi 4位码）
         *3、CPU-IMEI-设备类型-检测类型-NB卡z号（5位码）
         * */
        String[] codeArr = code.split("-");
        SDevice newDevice = new SDevice();
        //List<SDeviceDTO> devices = null;
        SDeviceDTO sDeviceDTO = null;
        //step0: 查看数据库里是否存在改设备
        if (codeArr.length == 3) {
            log.debug("qrcode  imei>>>>"+codeArr[1]);
            sDeviceDTO = sDeviceService.findByImei(codeArr[1]);
        }else {
            log.debug("qrcode  deviceid>>>>"+codeArr[0]);
            sDeviceDTO = sDeviceService.findByDeviceid(codeArr[0]);
        }
        log.debug("query devices by Imei and Deviceid >>>>"+sDeviceDTO);
        if (sDeviceDTO == null) {
            //---------------------------------新设备---------------------------------
            String deviceid = UUID.randomUUID().toString();
            newDevice.setId(deviceid);
            newDevice.setDeviceid(codeArr[0]);
            if(name==null || name.length()==0){
                newDevice.setName(codeArr[0]);
            }else{
                newDevice.setName(name);
            }
//            newDevice.setName(codeArr[0]);
            newDevice.setJd(Double.parseDouble(jd));
            newDevice.setWd(Double.parseDouble(wd));
            newDevice.setValid("1");
            newDevice.setRssi("30");
            newDevice.setBattery("5");
            newDevice.setQrcode(code);
            //step1: 如果数据库不存在该设备 存储设备到设备表
            if (codeArr.length == 3) {
                //nb设备采集上传频率3600000 = 1小时
                newDevice.setTimetrack(3600000);
                newDevice.setTimetrack2(3600000);
                //step2: 判断是不是电信NB设备，如果是电信NB需要调用平台接口注册设备和修改设备信息
                String imei = codeArr[1];
                Boolean imetCheck = ImeiUtil.genCode(imei);
                if (!imetCheck){
                    return ResponseEntity.badRequest().body("IMEI码不合法!");
                }
                newDevice.setImei(imei);
                newDevice.setType("0");
                newDevice.setDevicetype(String.valueOf(Integer.valueOf(codeArr[2].toString()) + 1));
                //获取设备类型的传感器标识
                SDevicetypeDTO sDevicetypeDTO = sDevicetypeService.findById((long)(Integer.valueOf(codeArr[2].toString()) + 1));
                if(sDevicetypeDTO == null){
                    log.error("the devicetype id ："+(Integer.valueOf(codeArr[2].toString()) + 1)+"is not exist！");
                }else {
                    newDevice.setGs(sDevicetypeDTO.getGs());
                    newDevice.setCode(sDevicetypeDTO.getCode());
                    newDevice.setSubject(sDevicetypeDTO.getSubject());
                }
                //向NB平台注册设备
                Result addNB = null;
                try {
                    addNB = NbUtil.addDeviceToNB(imei);
                } catch (Exception e) {
                    log.debug("NbUtil.addDeviceToNB(imei)>>>>"+e.toString());
                    e.printStackTrace();
                }
                if (addNB.getCode() == 200) { //注册成功,返回200
                    Map<String, Object> addMap = (Map<String, Object>) addNB.getData();
                    String telecomCode = (String) addMap.get("deviceId");
                    newDevice.setTelecomcode(telecomCode);
                    Result editDevice = null; //调用编辑接口给注册到平台的设备设置相关属性
                    try {
                        editDevice = NbUtil.editDeviceToNB(telecomCode);
                    } catch (Exception e) {
                        log.debug("NbUtil.editDeviceToNB(imei)>>>>"+e.toString());
                        e.printStackTrace();
                    }
                    if (editDevice.getCode() != 200) {
                        return ResponseEntity.status(1001).body("修改注册设备失败!");
                    }
                } else {
                    return ResponseEntity.status(1002).body("注册设备失败!");
                }
            }else if(codeArr.length == 4){
                log.info("codeArr.length>>>: "+codeArr.length);
                //获取设备类型
                String type = codeArr[2];
                log.debug("type>>>:"+type);
                String deviceType = String.valueOf(Integer.valueOf(codeArr[3].toString()) + 1);
                newDevice.setDevicetype(deviceType);
                //查询对应m_devicetype 并设置gs code subject字段
                log.debug("devicetype:select m_devicetype and set gs code subject cloumn"+(Integer.valueOf(codeArr[3].toString()) + 1));
                SDevicetypeDTO sDevicetypeDTO = sDevicetypeService.findById((long)(Integer.valueOf(codeArr[3].toString()) + 1));
                if(sDevicetypeDTO == null){
                    log.error("the devicetype id ："+(Integer.valueOf(codeArr[3].toString()) + 1)+"not exist！");
                }else {
                    newDevice.setGs(sDevicetypeDTO.getGs());
                    newDevice.setCode(sDevicetypeDTO.getCode());
                    newDevice.setSubject(sDevicetypeDTO.getSubject());
                }

                if(("0").equals(type)){//0->NB
                    //CPU-IMEI/4G卡号-设备类型-检测类型（4位码）
                    String imei = codeArr[1];
                    Boolean imetCheck = ImeiUtil.genCode(imei);
                    newDevice.setType(type);
                    String[] relayTypes = contains.split(",");
                    // 循环 配置文件中的 继电器 类型
                    for(int i=0;i<relayTypes.length;i++){
                        if((relayTypes[i].trim()).equals(deviceType)){
                            // 控制器采集上传频率
                            newDevice.setTimetrack(30000);
                            newDevice.setTimetrack2(30000);
                            //默认本地控制模式 workmode 工作模式 0远程 1本地
                            if("202".equals(deviceType)){
                                newDevice.setWorkmode("0");
                            }else{
                                newDevice.setWorkmode("1");
                            }
                            newDevice.setRelay("1");
                            //如果是控制器需要增加控制器支路表
                            createDeviceWay(newDevice.getDeviceid(), deviceType);
                            break;
                        }else{
                           //nb设备采集上传频率3600000 = 1小时
                            newDevice.setTimetrack(3600000);
                            newDevice.setTimetrack2(3600000);
                            newDevice.setRelay("0");
                        }
                    }
                    if (!imetCheck){
                        Map<String,String>map = new HashMap<>();
                        map.put("message","IMEI码不合法!");
                        return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
//                        return ResponseEntity.badRequest().body("IMEI码不合法!");
                    }
                    newDevice.setImei(imei);
                    //向NB平台注册设备
                    Result addNB = null;
                    try {
                        addNB = NbUtil.addDeviceToNB(imei);
                    } catch (Exception e) {
                        log.debug("NbUtil.addDeviceToNB(imei)>>>>"+e.toString());
                        e.printStackTrace();
                    }
                    if (addNB.getCode() == 200) { //注册成功,返回200
                        Map<String, Object> addMap = (Map<String, Object>) addNB.getData();
                        String telecomCode = (String) addMap.get("deviceId");
                        newDevice.setTelecomcode(telecomCode);
                        Result editDevice = null; //调用编辑接口给注册到平台的设备设置相关属性
                        try {
                            editDevice = NbUtil.editDeviceToNB(telecomCode);
                        } catch (Exception e) {
                            log.debug("NbUtil.editDeviceToNB(imei)>>>>"+e.toString());
                            e.printStackTrace();
                        }
                        if (editDevice.getCode() != 200) {
                            Map<String,String>map = new HashMap<>();
                            map.put("message","修改注册设备失败!");
                            return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
//                            return ResponseEntity.status(1001).body("修改注册设备失败!");
                        }
                    } else {
                        Map<String,String>map = new HashMap<>();
                        map.put("message","I注册设备失败!");
                        return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
//                        return ResponseEntity.status(1002).body("注册设备失败!");
                    }
                }else if(("1").equals(type)||("2").equals(type)|| ("4").equals(type)){//1->4G 2-> WiFi 4->以太网
                    log.debug("codeArr[3]:"+Integer.valueOf(codeArr[3].toString()) + 1);
                    newDevice.setPhonecode(codeArr[1]);
                    newDevice.setType(type);
                    String[] relayTypes = contains.split(",");
                    for(int i=0;i<relayTypes.length;i++){
                        if((relayTypes[i].trim()).equals(deviceType)){
                            // 控制器采集上传频率
                            newDevice.setTimetrack(30000);
                            newDevice.setTimetrack2(30000);
                            //默认本地控制模式 workmode 工作模式 0远程 1本地
                            if("202".equals(deviceType)){
                                newDevice.setWorkmode("0");
                            }else{
                                newDevice.setWorkmode("1");
                            }
                            newDevice.setRelay("1");
                            //如果是控制器需要增加控制器支路表
                            createDeviceWay(newDevice.getDeviceid(), deviceType);
                            break;
                        }else{
                            //4G设备 采集上传频率
                            newDevice.setTimetrack(300000);
                            newDevice.setTimetrack2(1800000);
                            newDevice.setRelay("0");
                        }
                    }
                    log.debug("newDevice:"+newDevice);
                } else {
                    Map<String,String>map = new HashMap<>();
                    map.put("message","设备类型type未添加，请联系管理员添加！ "+ type);
                    return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
                }
                // 循环 配置文件中的 LED屏幕 类型
                String[] LedCons = LedContains.split(",");
                for(int i=0;i<LedCons.length;i++){
                    if((LedCons[i].trim()).equals(deviceType)){
                        newDevice.setRelay("2");
                        break;
                    }
                }
            }else if(codeArr.length == 5){
                log.info("codeArr.length>>>: "+codeArr.length);
                //获取设备类型
                String type = codeArr[2];
                log.debug("type>>>:"+type);
                //查询对应m_devicetype 并设置gs code subject字段
                String deviceType = String.valueOf(Integer.valueOf(codeArr[3].toString()) + 1);
                newDevice.setDevicetype(String.valueOf(Integer.valueOf(codeArr[3].toString()) + 1));
                //log.debug("设备类型:查询对应m_devicetype 并设置gs code subject字段"+(Integer.valueOf(codeArr[3].toString()) + 1));
                SDevicetypeDTO sDevicetypeDTO = sDevicetypeService.findById((long)(Integer.valueOf(codeArr[3].toString()) + 1));
                if(sDevicetypeDTO == null){
                    log.error("the devicetype id ："+(Integer.valueOf(codeArr[3].toString()) + 1)+"not exist！");
                }else {
                    newDevice.setGs(sDevicetypeDTO.getGs());
                    newDevice.setCode(sDevicetypeDTO.getCode());
                    newDevice.setSubject(sDevicetypeDTO.getSubject());
                }
                newDevice.setPhonecode(codeArr[4]);
                if (("0").equals(type)) { //0->NB
                    //CPU-IMEI-设备类型-检测类型-NB卡号（5位码）
                    String imei = codeArr[1];
                    Boolean imetCheck = ImeiUtil.genCode(imei);
                    newDevice.setType(type);
                    // 循环 配置文件中的 继电器 类型
                    String[] relayTypes = contains.split(",");
                    for(int i=0;i<relayTypes.length;i++){
                        if((relayTypes[i].trim()).equals(deviceType)){
                            // 控制器采集上传频率
                            newDevice.setTimetrack(30000);
                            newDevice.setTimetrack2(30000);
                            //默认本地控制模式 workmode 工作模式 0远程 1本地
                            if("202".equals(deviceType)){
                                newDevice.setWorkmode("0");
                            }else{
                                newDevice.setWorkmode("1");
                            }
                            newDevice.setRelay("1");
                            //如果是控制器需要增加控制器支路表
                            createDeviceWay5QR(newDevice.getDeviceid(), deviceType);
                            break;
                        }else{
                            //nb设备采集上传频率3600000 = 1小时
                            newDevice.setTimetrack(3600000);
                            newDevice.setTimetrack2(3600000);
                            newDevice.setRelay("0");
                        }
                    }
                    if (!imetCheck){
                        Map<String,String>map = new HashMap<>();
                        map.put("message","IMEI码不合法!");
                        return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
//                        return ResponseEntity.badRequest().body("IMEI码不合法!");
                    }
                    newDevice.setImei(imei);
//                    //向NB平台注册设备
//                    Result addNB = null;
//                    try {
//                        addNB = NbUtil.addDeviceToNB(imei);
//                    } catch (Exception e) {
//                        log.debug("NbUtil.addDeviceToNB(imei)>>>>"+e.toString());
//                        e.printStackTrace();
//                    }
//                    if (addNB.getCode() == 200) { //注册成功,返回200
//                        Map<String, Object> addMap = (Map<String, Object>) addNB.getData();
//                        String telecomCode = (String) addMap.get("deviceId");
//                        newDevice.setTelecomcode(telecomCode);
//                        Result editDevice = null; //调用编辑接口给注册到平台的设备设置相关属性
//                        try {
//                            editDevice = NbUtil.editDeviceToNB(telecomCode);
//                        } catch (Exception e) {
//                            log.debug("NbUtil.editDeviceToNB(imei)>>>>"+e.toString());
//                            e.printStackTrace();
//                        }
//                        if (editDevice.getCode() != 200) {
//                            Map<String,String>map = new HashMap<>();
//                            map.put("message","修改注册设备失败!");
//                            return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
////                            return ResponseEntity.status(1001).body("修改注册设备失败!");
//                        }
//                    } else {
//                        Map<String,String>map = new HashMap<>();
//                        map.put("message","注册设备失败!");
//                        return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
////                        return ResponseEntity.status(1002).body("注册设备失败!");
//                    }
                }else if(("1").equals(type)||("2").equals(type)|| ("4").equals(type)){//1->4G 2-> WiFi 4->以太网
                    log.debug("codeArr[3]:"+Integer.valueOf(codeArr[3].toString()) + 1);
                    newDevice.setPhonecode(codeArr[1]);
                    newDevice.setTelecomcode(codeArr[4]);
                    newDevice.setType(type);
                    // 循环 配置文件中的 继电器 类型
                    String[] relayTypes = contains.split(",");
                    for(int i=0;i<relayTypes.length;i++){
                        if(relayTypes[i].equals(deviceType)){
                            // 控制器采集上传频率
                            newDevice.setTimetrack(30000);
                            newDevice.setTimetrack2(30000);
                            //默认本地控制模式 workmode 工作模式 0远程 1本地
                            if("202".equals(deviceType)){
                                newDevice.setWorkmode("0");
                            }else{
                                newDevice.setWorkmode("1");
                            }
                            newDevice.setRelay("1");
                            //如果是控制器需要增加控制器支路表
                            createDeviceWay5QR(newDevice.getDeviceid(), deviceType);
                            break;
                        }else{
                            //4G设备 采集上传频率
                            newDevice.setTimetrack(300000);
                            newDevice.setTimetrack2(1800000);
                            newDevice.setRelay("0");
                        }
                    }
                    log.debug("newDevice:"+newDevice);
                } else {
                    Map<String,String>map = new HashMap<>();
                    map.put("message","设备类型type未添加，请联系管理员添加！ "+ type);
                    return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
                }
                // 循环 配置文件中的 LED屏幕 类型
                String[] LedCons = LedContains.split(",");
                for(int i=0;i<LedCons.length;i++){
                    if((LedCons[i].trim()).equals(deviceType)){
                        newDevice.setRelay("2");
                        break;
                    }
                }
            }else{
//                return ResponseEntity.badRequest().body("the length of codeArr is "+codeArr.length+" ,should be 3 or 4or 5!");
                Map<String,String>map = new HashMap<>();
                map.put("message","二维码信息错误，请检查后再次确认！长度为："+codeArr.length);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }
            sDeviceService.create(newDevice);
            //step2、把设备绑定给当前项目
            JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
            SProjectDevice sProjectDevice = new SProjectDevice();
            sProjectDevice.setProjectid(projectid);
            sProjectDevice.setDeviceid(newDevice.getDeviceid());
            sProjectDevice.setState("1");
            sProjectDevice.setValid("1");
            sProjectDeviceService.create(sProjectDevice);
            log.debug("project deviceid binding successful!"+projectid+" "+newDevice.getDeviceid());
            return ResponseEntity.status(HttpStatus.OK).body("bingding successful existingdevice!");
        }else {
            //---------------------------------数据库表已经存在该设备---------------------------------
            newDevice = sDeviceMapper.toEntity(sDeviceDTO);
            String devicetypeold = sDeviceDTO.getDevicetype();
            //1、更新devicetype及gs code subject（过去式  不更新了，提示删除后重新添加）
            if (codeArr.length == 3) {
                String devicetypenew = String.valueOf(Integer.valueOf(codeArr[2].toString()) + 1);
                if(!devicetypeold.equals(devicetypenew)){
                    Map<String,String>map = new HashMap<>();
                    map.put("message","设备类型已更改，请删除设备后重新添加！"+devicetypeold+","+devicetypenew);
                    return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
                }
                /*newDevice.setDevicetype(String.valueOf(Integer.valueOf(codeArr[2].toString()) + 1));
                //获取设备类型的传感器标识
                SDevicetypeDTO sDevicetypeDTO = sDevicetypeService.findById((long)(Integer.valueOf(codeArr[2].toString()) + 1));
                if(sDevicetypeDTO == null){
                    log.error("the devicetype id ："+(Integer.valueOf(codeArr[2].toString()) + 1)+"is not exist！");
                }else {
                    newDevice.setGs(sDevicetypeDTO.getGs());
                    newDevice.setCode(sDevicetypeDTO.getCode());
                    newDevice.setSubject(sDevicetypeDTO.getSubject());
                }
                sDeviceService.update(newDevice);*/
            }else if(codeArr.length == 5 || codeArr.length == 4){
                String devicetypenew = String.valueOf(Integer.valueOf(codeArr[3].toString()) + 1);
                if(!devicetypeold.equals(devicetypenew)){
                    Map<String,String>map = new HashMap<>();
                    map.put("message","设备类型已更改，请删除设备后重新添加！"+devicetypeold+","+devicetypenew);
                    return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
                }
               /* //查询对应m_devicetype 并设置gs code subject字段
                newDevice.setDevicetype(String.valueOf(Integer.valueOf(codeArr[3].toString()) + 1));
                log.debug("设备类型:查询对应m_devicetype 并设置gs code subject字段"+(Integer.valueOf(codeArr[3].toString()) + 1));
                SDevicetypeDTO sDevicetypeDTO = sDevicetypeService.findById((long)(Integer.valueOf(codeArr[3].toString()) + 1));
                if(sDevicetypeDTO == null){
                    log.error("the devicetype id ："+(Integer.valueOf(codeArr[3].toString()) + 1)+"not exist！");
                }else {
                    newDevice.setGs(sDevicetypeDTO.getGs());
                    newDevice.setCode(sDevicetypeDTO.getCode());
                    newDevice.setSubject(sDevicetypeDTO.getSubject());
                }
                sDeviceService.update(newDevice);*/
            }
            //       查看 项目 【用户】 是否已经绑定过防止重复绑定
            //2: 查看 项目 【设备】 是否已经绑定过防止重复绑定
            //List<SProjectUser> spuser = muserdeviceService.query(Cnd.where("deviceid", "=", newDevice.getId()).and("userid", "=", userid));
            SProjectDeviceDTO sProjectDeviceDTO = sProjectDeviceService.findByProjectidAndDeviceid(projectid,newDevice.getDeviceid());
            if (sProjectDeviceDTO!=null){
                if("0".equals(sProjectDeviceDTO.getValid())){//设备处于解除绑定状态
                    //将设备与设备绑定表状态更新为绑定状态
                    sProjectDeviceDTO.setValid("1");
                    SProjectDevice sProjectDevice = new SProjectDevice();
                    sProjectDevice = sProjectDeviceMapper.toEntity(sProjectDeviceDTO);
                    sProjectDeviceService.update(sProjectDevice);
                    log.debug("设备重新绑定成功!"+newDevice.getId()+" "+projectid);
                    return ResponseEntity.status(HttpStatus.OK).body("设备绑定成功!");
                }else{
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("你已经绑定过该设备,设备名称为：" + sDeviceDTO.getName() + "。");
                    log.info("你已经绑定过该设备,设备名称为：" + sDeviceDTO.getName());
                    Map<String,String>map = new HashMap<>();
                    map.put("message","本账户已绑定过该设备,设备名称为：" + sDeviceDTO.getName());
                    return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
                }
            }else{
                //step2、查询设备对应的项目id
                SProjectDeviceDTO sProjectDeviceDTO1 = sProjectDeviceService.findByDeviceid(newDevice.getDeviceid());
                String newprojectid = null;
                if(sProjectDeviceDTO1==null){
                    newprojectid = projectid;
                    //3: 建立绑定关系
                    SProjectDevice sProjectDevice = new SProjectDevice();
                    sProjectDevice.setProjectid(newprojectid);
                    sProjectDevice.setDeviceid(newDevice.getDeviceid());
                    sProjectDevice.setState("1");
                    sProjectDevice.setValid("1");
                    sProjectDeviceService.create(sProjectDevice);
                    log.debug("project Device binding successful!"+newDevice.getDeviceid()+" "+newprojectid);
                }else{
                    newprojectid = sProjectDeviceDTO1.getProjectid();
                    // 把项目绑定到当前用户
                    JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
                    String userid = jwtUser.getId().toString();
                    SProjectUserDTO sProjectUserDTO = sProjectUserService.findByProjectidAndUserid(newprojectid, userid);
                    if(sProjectUserDTO==null){
                        SProjectUser sProjectUser = new SProjectUser();
                        sProjectUser.setProjectid(newprojectid);
                        sProjectUser.setUserid(userid);
                        sProjectUser.setState("1");
                        sProjectUser.setValid("1");
                        sProjectUserService.create(sProjectUser);
                        log.debug("project Device binding successful!"+newprojectid+" "+userid);
                    }else{
                        Map<String,String>map = new HashMap<>();
                        map.put("message","本账户其他项目已绑定过该设备,设备名称为：" + sDeviceDTO.getName());
                        return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body("bingding successful newdevice!");
            }
        }
    }

    /**
     * 二维码是4位时 如果设备是控制器需要增加控制器支路表
     * @param deviceid
     * @param deviceType
     */
    private void createDeviceWay(String deviceid, String deviceType) {
        log.info("控制器类型为："+deviceType);
        String newDeviceType =  new StringBuffer(deviceType).reverse().toString();
        String dt = newDeviceType.substring(2);
        String dtype = new StringBuffer(dt).reverse().toString();
        log.info("控制器类型支路个数为："+dtype);
        for(int i=1;i<=Integer.parseInt( dtype );i++){
            SDeviceWay sDeviceWay = new SDeviceWay();
            sDeviceWay.setDeviceid(deviceid);
            sDeviceWay.setWayname("控制器"+i+"路");
            sDeviceWay.setWay(i+"");
            sDeviceWay.setStatus("0");//添加上是关闭状态
            //sDeviceWay.setMsg(deviceType);
            sDeviceWayService.create(sDeviceWay);
        }
    }

    /**
     * 二维码是5位时 如果设备是控制器需要增加控制器支路表
     * @param deviceid
     * @param deviceType
     */
    private void createDeviceWay5QR(String deviceid, String deviceType) {
        log.info("控制器类型为："+deviceType);
//        String newDeviceType =  new StringBuffer(deviceType).reverse().toString();
        String dtype = deviceType.substring(1);
        int type = Integer.parseInt(dtype)-1;
//        String dtype = new StringBuffer(dt).reverse().toString();
        log.info("控制器类型支路个数为："+dtype);
        for(int i=1;i<=type;i++){
            SDeviceWay sDeviceWay = new SDeviceWay();
            sDeviceWay.setDeviceid(deviceid);
            sDeviceWay.setWayname("控制器"+i+"路");
            sDeviceWay.setWay(i+"");
            sDeviceWay.setStatus("0");//添加上是关闭状态
            //sDeviceWay.setMsg(deviceType);
            sDeviceWayService.create(sDeviceWay);
        }
    }
    /**
     * 按用户查询控制器分页
     * @param criteria
     * @return
     */
    @Log("查询sDeviceByUseridAnddevType")
    @ApiOperation(value = "查询sDeviceByUseridAnddevType")
    @GetMapping(value = "/sDeviceByUseridAnddevType")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_SELECT')")
    public ResponseEntity sDeviceByUseridAnddevType(SDeviceQueryCriteria criteria, Pageable pageable){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        String relay = "1";
        return new ResponseEntity(sDeviceService.queryAllByUseridAnddevType(userid, relay, criteria,pageable),HttpStatus.OK);
    }
    /**
     * 按用户查询设备分页
     * @param criteria
     * @return
     */
    @Log("查询sDeviceByUseridNotdevType")
    @ApiOperation(value = "查询sDeviceByUseridNotdevType")
    @GetMapping(value = "/sDeviceByUseridNotdevType")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICE_ALL','SDEVICE_SELECT')")
    public ResponseEntity sDeviceByUseridNotdevType(SDeviceQueryCriteria criteria, Pageable pageable){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        String relay = "0";
        return new ResponseEntity(sDeviceService.queryAllByUseridNotdevType(userid, relay, criteria,pageable),HttpStatus.OK);
    }

    @Log("查询loaddevice")
    @ApiOperation(value = "查询loaddevice")
    @PostMapping(value = "/loaddevice")
    public ResponseEntity loaddevice(SDeviceQueryCriteria criteria){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        String jobName = jwtUser.getJob();
        Boolean admin = jobName.contains("admin");
        List<SDeviceDTO> list = null;
        if(admin){//部门管理员
            //包含admin字符是部门管理员加载本部门及本部门以下部门人员的设备
        }else{//普通用户
            //普通用户只加载本人绑定的设备
            list = sDeviceService.queryAllByUseridNoPage(userid);
        }
        //sDeviceService.queryAllByUseridNotdevType(userid, devicetype, criteria,pageable),
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * 按用户查询设备不分页
     * @param criteria
     * @return
     */
    @Log("查询queryAllByUseridNoPage")
    @ApiOperation(value = "查询queryAllByUseridNoPage")
    @PostMapping(value = "/queryAllByUseridNoPage")
    public ResponseEntity queryAllByUseridNoPage(SDeviceQueryCriteria criteria){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        List<SDeviceDTO> list = sDeviceService.queryAllByUseridNoPage(userid);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * 按用户查询控制器不分页
     * @param criteria
     * @return
     */
    @Log("查询queryAllRelayByUseridNoPage")
    @ApiOperation(value = "查询queryAllRelayByUseridNoPage")
    @PostMapping(value = "/queryAllRelayByUseridNoPage")
    public ResponseEntity queryAllRelayByUseridNoPage(SDeviceQueryCriteria criteria){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        List<SDeviceDTO> list = sDeviceService.queryAllRelayByUseridNoPage(userid);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @Log("查询getOnOffCount")
    @ApiOperation(value = "查询getOnOffCount")
    @PostMapping(value = "/getOnOffCount")
    public ResponseEntity getOnOffCount(SDeviceQueryCriteria criteria){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        String onStr = "801on";
        String offStr = "801off";
        String devonStr = "devon";
        String devoffStr = "devoff";
        Integer onCOunt = sDeviceService.getOnCount(userid, onStr);
        Integer offCOunt = sDeviceService.getOnCount(userid, offStr);
        Integer devonCOunt = sDeviceService.getDevOnCount(userid, devonStr);
        Integer devoffCOunt = sDeviceService.getDevOnCount(userid, devoffStr);
        Map<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("relayon",onCOunt);
        returnMap.put("relayoff",offCOunt);
        returnMap.put("devon",devonCOunt);
        returnMap.put("devoff",devoffCOunt);
        return new ResponseEntity(returnMap, HttpStatus.OK);
    }

    /**
     * 按项目查询设备不分页
     * @return
     */
    @Log("getDeviceByProjectid")
    @ApiOperation(value = "getDeviceByProjectid")
    @GetMapping(value = "/getDeviceByProjectid/{projectid}")
    public ResponseEntity getDeviceByProjectid(@PathVariable Long projectid){
        List<SDeviceDTO> list = sDeviceService.getDeviceByProjectid(projectid);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * 按项目查询设备和控制器不分页
     * @return
     */
    @Log("getDeviceAndRelayByProjectid")
    @ApiOperation(value = "getDeviceAndRelayByProjectid")
    @GetMapping(value = "/getDeviceAndRelayByProjectid/{projectid}")
    public ResponseEntity getDeviceAndRelayByProjectid(@PathVariable Long projectid){
        List<SDeviceDTO> list = sDeviceService.getDeviceAndRelayByProjectid(projectid);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * 查询天气接口
     * @return
     */
    @Log("weather")
    @ApiOperation(value = "weather")
    @PostMapping(value = "/weather", produces="text/html;charset=utf-8")
    public ResponseEntity weather(@Validated @RequestBody Map map){
        String url = "https://i.tianqi.com/";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("c", map.get("c"));
        paramMap.put("a", map.get("a"));
        paramMap.put("id", map.get("id"));
        paramMap.put("icon",map.get("icon"));
        String result = HttpUtil.get(url, paramMap);
        return new ResponseEntity(result, HttpStatus.OK);
    }
}