package me.zhengjie.modules.smart.rest;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.aop.log.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.security.JwtUser;
import me.zhengjie.modules.smart.domain.SDevice;
import me.zhengjie.modules.smart.domain.SDeviceLog;
import me.zhengjie.modules.smart.domain.SDeviceWay;
import me.zhengjie.modules.smart.domain.STaskWay;
import me.zhengjie.modules.smart.service.SDeviceLogService;
import me.zhengjie.modules.smart.service.SDeviceService;
import me.zhengjie.modules.smart.service.SDeviceWayService;
import me.zhengjie.modules.smart.service.dto.SDeviceDTO;
import me.zhengjie.modules.smart.service.dto.SDeviceWayDTO;
import me.zhengjie.modules.smart.service.dto.SDeviceWayQueryCriteria;
import me.zhengjie.modules.smart.service.dto.STaskWayDTO;
import me.zhengjie.modules.smart.utils.ControlPLCWayConstants;
import me.zhengjie.modules.system.service.dto.RoleSmallDTO;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.util.*;

/**
* @author lhl
* @date 2019-10-23
*/
@Slf4j
@Api(tags = "SDeviceWay管理")
@RestController
@RequestMapping("api")
public class SDeviceWayController {

    @Autowired
    private SDeviceWayService sDeviceWayService;
    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private SDeviceLogService sDeviceLogService;
    @Value("${relay.address}")
    private String address;
    @Value("${relay.addressNR}")
    private String addressNR;
    @Value("${relay.CangmaPLC}")
    private String CangmaPLC;
    @Autowired
    private SDeviceService sDeviceService;

    @Log("查询SDeviceWay")
    @ApiOperation(value = "查询SDeviceWay")
    @GetMapping(value = "/sDeviceWay")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICEWAY_ALL','SDEVICEWAY_SELECT')")
    public ResponseEntity getSDeviceWays(SDeviceWayQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sDeviceWayService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询SDeviceWayNoPage")
    @ApiOperation(value = "查询SDeviceWayNoPage")
    @PostMapping(value = "/sDeviceWayNoPage")
    public ResponseEntity getSDeviceWaysNoPage(){
        SDeviceWayQueryCriteria criteria = new SDeviceWayQueryCriteria();
        return new ResponseEntity(sDeviceWayService.queryAll(criteria),HttpStatus.OK);
    }

    @Log("新增SDeviceWay")
    @ApiOperation(value = "新增SDeviceWay")
    @PostMapping(value = "/sDeviceWay")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICEWAY_ALL','SDEVICEWAY_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SDeviceWay resources){
        return new ResponseEntity(sDeviceWayService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SDeviceWay")
    @ApiOperation(value = "修改SDeviceWay")
    @PutMapping(value = "/sDeviceWay")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICEWAY_ALL','SDEVICEWAY_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SDeviceWay resources){
        sDeviceWayService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SDeviceWay")
    @ApiOperation(value = "删除SDeviceWay")
    @DeleteMapping(value = "/sDeviceWay/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICEWAY_ALL','SDEVICEWAY_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sDeviceWayService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("删除deleteByDeviceid")
    @ApiOperation(value = "删除deleteByDeviceid")
    @DeleteMapping(value = "/deleteByDeviceid/{deviceid}")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICEWAY_ALL','SDEVICEWAY_DELETE')")
    public ResponseEntity deleteByDeviceid(@PathVariable String deviceid){
        sDeviceWayService.deleteByDeviceid(deviceid);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("loadByDeviceid")
    @ApiOperation(value = "loadByDeviceid")
    @PostMapping(value = "/loadByDeviceid")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICEWAY_ALL','SDEVICEWAY_EDIT')")
    public ResponseEntity loadByDeviceid(@Validated @RequestBody SDeviceWay resources){
        String deviceid = resources.getDeviceid();
        List<SDeviceWayDTO> list = sDeviceWayService.findByDeviceid(deviceid);
        String stausstr = "";
        for(int i=0;i<list.size();i++){
            stausstr = list.get(i).getStatus() + stausstr ;
        }
        for(int i=0;i<list.size();i++){
            list.get(i).setMsg(stausstr);
        }
        return new ResponseEntity(list,HttpStatus.OK);
    }

    @Log("loadTaskwayByDeviceid")
    @ApiOperation(value = "loadTaskwayByDeviceid")
    @GetMapping(value = "/loadTaskwayByDeviceid/{relayid}")
    public ResponseEntity loadTaskwayByDeviceid(@PathVariable String relayid){
        String deviceid = relayid;
        List<SDeviceWayDTO> list = sDeviceWayService.findByDeviceid(deviceid);
        List<STaskWayDTO> sTaskList = new ArrayList<STaskWayDTO>();
        for(int i=0;i<list.size();i++){
            STaskWayDTO sTaskWayDTO = new STaskWayDTO();
            sTaskWayDTO.setWayname(list.get(i).getWayname());
            sTaskWayDTO.setWay(list.get(i).getWay());
            sTaskWayDTO.setStatus("0");
            sTaskWayDTO.setValid("0");
            sTaskWayDTO.setNt(list.get(i).getId().toString());
            sTaskList.add(sTaskWayDTO);
        }
        return new ResponseEntity(sTaskList,HttpStatus.OK);
    }

    @Log("controlWay")
    @ApiOperation(value = "controlWay")
    @PostMapping(value = "/controlWay")
    @PreAuthorize("hasAnyRole('ADMIN','SDEVICEWAY_ALL','SDEVICEWAY_EDIT')")
    public ResponseEntity controlWay(@Validated @RequestBody SDeviceWay resources){
        String deviceid = resources.getDeviceid();
//        String msg = resources.getMsg();
        String way = resources.getWay();
        String status = resources.getStatus();
        int wayInt = Integer.parseInt(way);
        SDeviceDTO sDevice = sDeviceService.findByDeviceid(deviceid);
        String devicetype = sDevice.getDevicetype();
        // 202,网络继电器
        if("202".equals(devicetype)){
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("relayId", deviceid);
            paramMap.put("way", way);
            paramMap.put("status", status);
            String url = addressNR;//8007
            log.info("202请求地址："+url);
            return getHttpRequeat(resources, paramMap, url);
        }else{
            // 循环 配置文件中的 PLC 设备ID
            String[] plcTypes = CangmaPLC.split(",");
            List<String> plcList = Arrays.asList(plcTypes);
            if(plcList.contains(deviceid)) { // 包含青岛藏马山两个设备
                Boolean flag = false;
                String deviceidWay = deviceid+way;
                //获取item对应的寄存器地址
                JSONObject jsonObject = JSONUtil.parseObj(ControlPLCWayConstants.WAY_TO_ADDRESS);
                String register = String.valueOf(jsonObject.get(deviceidWay)); //寄存器地址
                //      地址 个数 字节 数据  CRC
                // 0110 A080 0001  02  0001
                String cmd = "0110";
                String hexVale =  status;
                if(register.length()>4){
                    cmd = cmd + register.substring(0,4);
                    String cmmd = register.substring(4);
                    if("1".equals(status)){
                        flag = true;
                        hexVale = cmmd;
                    }
                }else{
                    cmd = cmd + register;
                }
                if (hexVale.length() < 4) {
                    hexVale = StringUtils.leftPad(hexVale, 4, "0").toUpperCase();
                }
                log.info("hexVale:{}", hexVale);
                String closeCmd = cmd +"000102" + "0000";
                cmd = cmd +"000102" + hexVale;
                if(flag){
                    log.info("开启正转反转之前调用关闭 向{}控制器发送指令:{}",deviceid,closeCmd);
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("relayId", deviceid);
                    paramMap.put("command", closeCmd);
                    String url = address;//8007
//                    log.info("请求地址："+url);
//                return ResponseEntity.status(200).body("success");
                    ResponseEntity responseEntity =getHttpRequeat(resources, paramMap, url);
                    responseEntity.getStatusCode();
                    responseEntity.getBody().toString();
                    log.info("开启正转反转之前调用关闭 请求地址："+responseEntity.getBody().toString());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("向{}控制器发送指令:{}",deviceid,cmd);
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("relayId", deviceid);
                paramMap.put("command", cmd);
                String url = address;//8007
                log.info("请求地址："+url);
//                return ResponseEntity.status(200).body("success");
                return getHttpRequeat(resources, paramMap, url);
            }else{
                // 工作模式为本地  无法操作
                if(("1").equals(sDevice.getWorkmode())){
                    Map<String,String>map = new HashMap<>();
                    map.put("message","本地控制模式，无法操作请刷新页面！");
                    return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
                }
                // 青岛藏马山项目功能
                String userjob = SecurityUtils.getUserJob();
                if("panoramascreen".equals(userjob)){
                    // 藏马山控制器控制判断
                    CangMaShanPanDuan(deviceid, way, status, sDevice);
                }
                List<SDeviceWayDTO> list = sDeviceWayService.findByDeviceid(deviceid);
                //1、初始状态 只有第一路开      如：10000000  开关关关关关关关
                //2、查询支路状态 倒序组合      如：00000001  关关关关关关关开
                String stausstr = "";
                for(int i=0;i<list.size();i++){
                    stausstr = list.get(i).getStatus() + stausstr;
                }
                //3、替换被操作的支路状态 三路开 如：00000101
                StringBuilder msgstr = new StringBuilder(stausstr);
                msgstr.replace((list.size()+1)-wayInt-1,(list.size()+1)-wayInt,status);
                //4、位数补足16位             如：        00000101
                String sendStatus = String.format("%16s", msgstr);
                //5、空格替换为0              如：0000000000000101
                sendStatus = sendStatus.replaceAll("\\s", "0");
                log.info("SDeviceWayController->controlWay->sendStatus:"+sendStatus);
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("relayId", deviceid);
                paramMap.put("command", sendStatus);
                String url = address;//8007
                log.info("请求地址："+url);
                return getHttpRequeat(resources, paramMap, url);
            }
        }
    }
    // 藏马山控制器控制判断
    private void CangMaShanPanDuan(String deviceid, String way, String status, SDeviceDTO sDevice) {
        Long userId = SecurityUtils.getUserId();
//        if("cangma".equals(userId)){
        String telecomcode = sDevice.getTelecomcode();
            // 判断藏马山控制器类型1-7
        if(telecomcode == null){
            // 默认为 56个暖棚控制器
            log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);
            // 判断是那一路开关 1-6，并做控制逻辑
            RelayKongzhi(deviceid, way,  status);
        }else{
            switch (telecomcode) {
                case "1":
                    log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);
                    RelayKongzhiOne(deviceid, way,  status);
                    break;
                case "2":
                    log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);

                    break;
                case "3":
                    log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);
                    RelayKongzhiThree(deviceid, way,  status);
                    break;
                case "4":
                    log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);
                    RelayKongzhiOne(deviceid, way,  status);
                    break;
                case "5":
                    log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);
                    RelayKongzhiOne(deviceid, way,  status);
                    break;
                case "6":
                    log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);

                    break;
                case "7":
                    log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);

                    break;
                default:
                    // 默认为 56个暖棚控制器
                    log.info("藏马山控制器:{} 类型telecomcode:{}", deviceid, telecomcode);
                    // 判断是那一路开关 1-6，并做控制逻辑
                    RelayKongzhi(deviceid, way,  status);
                    break;
            }
        }
//        }
    }
    // 藏马山 1类控制器 控制逻辑
    private void RelayKongzhiOne(String deviceid, String way, String status) {
        String wayQuery = "";
        switch (way) {
            case "1":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "2";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "2":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "1";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "3":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "4";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "4":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "3";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "5":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "6";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "6":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "5";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "7":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "8";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "8":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "7";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "9":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "10";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "10":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "9";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "11":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "12";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "12":
                log.info("藏马山1类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "11";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            default:
                log.info("default藏马山1类控制器:{}支路:{}", deviceid ,way);
                break;
        }
    }

    // 藏马山 3类控制器 控制逻辑
    private void RelayKongzhiThree(String deviceid, String way, String status) {
        String wayQuery = "";
        switch (way) {
            case "1":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "2";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "2":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "1";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "3":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "4";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "4":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "3";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "5":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "6";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "6":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "5";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "7":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "8";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "8":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "7";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "9":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "10";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "10":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "9";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "11":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "12";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "12":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "11";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "13":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "12";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "14":
                log.info("藏马山3类控制器:{}支路:{}", deviceid ,way);
                wayQuery = "11";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            default:
                log.info("default藏马山3类控制器:{}支路:{}", deviceid ,way);
                break;
        }
    }

    // 判断是那一路开关 1-6，并做控制逻辑
    private void RelayKongzhi(String deviceid, String way, String status) {
        String wayQuery = "";
        switch (way) {
            case "1":
                log.info("藏马山控制器:{}支路:{}", deviceid ,way);
                wayQuery = "2";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "2":
                log.info("藏马山控制器:{}支路:{}", deviceid ,way);
                wayQuery = "1";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "3":
                log.info("藏马山控制器:{}支路:{}", deviceid ,way);
                wayQuery = "4";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "4":
                log.info("藏马山控制器:{}支路:{}", deviceid ,way);
                wayQuery = "3";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "5":
                log.info("藏马山控制器:{}支路:{}", deviceid ,way);
                wayQuery = "6";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            case "6":
                log.info("藏马山控制器:{}支路:{}", deviceid ,way);
                wayQuery = "5";
                // 控制逻辑
                KongZhi(deviceid, way, status, wayQuery);
                break;
            default:
                log.info("default藏马山控制器:{}支路:{}", deviceid ,way);
                break;
        }
    }
    // 控制逻辑
    private void KongZhi(String deviceid, String way, String status, String wayQuery) {
        if("1".equals(status)){ // 开1路
            // 满足两个条件
            // 1、2路没开
            SDeviceWayDTO sDeviceWayDTO1 = sDeviceWayService.findByDeviceidAndWay(deviceid, wayQuery);
            String statusDto = sDeviceWayDTO1.getStatus();
            if("1".equals(statusDto)){// 2路开着
                throw new BadRequestException("控制器"+ wayQuery +"路已开启，请等待！");
            }
            // 2、1路没到限位器
            SDeviceWayDTO sDeviceWayDTO2 = sDeviceWayService.findByDeviceidAndWay(deviceid, way);
            String msg = sDeviceWayDTO2.getMsg();
            if("1".equals(msg)){ // 1路已到达限位器
                throw new BadRequestException("控制器该支路已到限位器，请勿重复操作！");
            }
        }
    }

    private ResponseEntity getHttpRequeat(@RequestBody @Validated SDeviceWay resources, Map<String, Object> paramMap, String url) {
        try {
            String result = HttpUtil.get(url, paramMap);
            log.info("SDeviceWayController->controlWay->result:"+result);
            if("success".equals(result)){
                //1、更改状态
                if("0".equals(resources.getStatus())){
                    resources.setStatus("0");
                }else{
                    resources.setStatus("1");
                }
                resources.setMsg(null);
                sDeviceWayService.update(resources);
                //2、保存操作记录
                String message = "成功";
                createDeviceLog(resources, message);
                log.info("getHttpRequeat:"+message+" "+result);
                return ResponseEntity.status(200).body("success");
            }else if("fail".equals(result)){
                //1、保存操作记录
                String message = "发送失败";
                createDeviceLog(resources, message);
                Map<String,String>map = new HashMap<>();
                map.put("message","发送失败！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map , HttpStatus.BAD_REQUEST);
            }else if("exefail".equals(result)){
                //1、保存操作记录
                String message = "执行失败";
                createDeviceLog(resources, message);
                Map<String,String>map = new HashMap<>();
                map.put("message","执行失败！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }else if("timeout".equals(result)){
                //1、保存操作记录
                String message = "超时";
                createDeviceLog(resources, message);
                Map<String,String>map = new HashMap<>();
                map.put("message","超时失败！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }else if("offline".equals(result)){
                //保存任务执行记录
                String message = "设备离线";
                createDeviceLog(resources, message);
                Map<String,String>map = new HashMap<>();
                map.put("message","设备离线！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }else{
                String message = "请求错误";
                createDeviceLog(resources, message);
                Map<String,String>map = new HashMap<>();
                map.put("message","请求错误！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("SDeviceWayController->controlWay->catch:"+e.getMessage());
            String message = "请求失败";
            createDeviceLog(resources, message);
            Map<String,String>map = new HashMap<>();
            map.put("message","请求失败！");
            return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 保存操作日志
     * @param resources
     */
    private void createDeviceLog(@RequestBody @Validated SDeviceWay resources, String messgae) {
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        SDeviceLog sDeviceLog = new SDeviceLog();
        if("0".equals(resources.getStatus())){
            sDeviceLog.setAction("关闭");
        }else{
            sDeviceLog.setAction("打开");
        }
        sDeviceLog.setDeviceid(resources.getDeviceid());
        sDeviceLog.setUserid(userid);
        sDeviceLog.setMessage(messgae);
        sDeviceLog.setWay(resources.getWay());
        sDeviceLog.setWayname(resources.getWayname());
        sDeviceLogService.create(sDeviceLog);
    }

}