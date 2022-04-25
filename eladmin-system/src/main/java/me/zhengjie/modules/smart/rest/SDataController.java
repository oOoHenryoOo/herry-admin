package me.zhengjie.modules.smart.rest;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.aop.log.Log;
import com.alibaba.excel.ExcelWriter;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.security.security.JwtUser;
import me.zhengjie.modules.smart.domain.*;
import me.zhengjie.modules.smart.repository.SWarnLogRepository;
import me.zhengjie.modules.smart.service.*;
import me.zhengjie.modules.smart.service.dto.*;
import me.zhengjie.modules.smart.service.mapper.SDataMapper;
import me.zhengjie.modules.smart.service.mapper.SDeviceMapper;
import me.zhengjie.modules.smart.service.mapper.SDeviceWayMapper;
import me.zhengjie.modules.smart.service.mapper.SWarnWayMapper;
import me.zhengjie.modules.smart.utils.ControlPLCWayConstants;
import me.zhengjie.modules.smart.utils.SDKTestSendTemplateSMS;
import me.zhengjie.modules.smart.utils.WarnUtil;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.utils.ElAdminConstant;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
* @author lhl
* @date 2019-10-22
*/
@Slf4j
@Api(tags = "SData管理")
@RestController
@RequestMapping("api")
public class SDataController {

    @Autowired
    private SDataService sDataService;
    @Autowired
    private SDeviceService sDeviceService;
    @Autowired
    private SWarnService sWarnService;
    @Autowired
    private SWarnmsgService sWarnmsgService;
    @Autowired
    private SDevicetypeService sDevicetypeService;
    @Autowired
    private SDeviceMapper sDeviceMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private SProjectDeviceService sProjectDeviceService;
    @Autowired
    private SProjectService sProjectService;
    @Autowired
    private SDeviceWayService sDeviceWayService;
    @Autowired
    private SWarnWayService sWarnWayService;
    @Value("${relay.address}")
    private String address;
    @Value("${relay.addressNR}")
    static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
    @Autowired
    private SWarnLogService sWarnLogService;
    @Value("${relay.CangmaPLC}")
    private String cangmaPLC;
    @Resource
    private SWarnWayMapper sWarnWayMapper;
    @Resource
    private SWarnLogRepository sWarnLogRepository;

    @Log("查询SData")
    @ApiOperation(value = "查询SData")
    @GetMapping(value = "/sData")
    @PreAuthorize("hasAnyRole('ADMIN','SDATA_ALL','SDATA_SELECT')")
    public ResponseEntity getSDatas(SDataQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity(sDataService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增SData")
    @ApiOperation(value = "新增SData")
    @PostMapping(value = "/sData")
    @PreAuthorize("hasAnyRole('ADMIN','SDATA_ALL','SDATA_CREATE')")
    public ResponseEntity create(@Validated @RequestBody SData resources){
        return new ResponseEntity(sDataService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改SData")
    @ApiOperation(value = "修改SData")
    @PutMapping(value = "/sData")
    @PreAuthorize("hasAnyRole('ADMIN','SDATA_ALL','SDATA_EDIT')")
    public ResponseEntity update(@Validated @RequestBody SData resources){
        sDataService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除SData")
    @ApiOperation(value = "删除SData")
    @DeleteMapping(value = "/sData/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SDATA_ALL','SDATA_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        sDataService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    /*监听生产者推送的数据*/
    @KafkaListener(topics = ElAdminConstant.KAFKA_CONSUMER_TOPIC)
    public void consumerData(ConsumerRecord<?, ?> consumer) {
        log.info("kafka监听:标签:{} - key:{} - 数据:{}", consumer.topic(), consumer.key(), consumer.value());
        Map<String, Object> rawdataMap = new HashMap();
        rawdataMap = JSON.parseObject(consumer.value().toString(), rawdataMap.getClass()); //上传的原始数据
        log.info("上传的原始数据:{}", rawdataMap);
        String U = rawdataMap.get("U").toString(); //获取CPU编号
        log.info("CPU编号:{}",U);
        SDeviceDTO sDeviceDTO = sDeviceService.findByDeviceid(U);
        /*如果设备不存在或者设备已经无效 则不处理*/
        if (sDeviceDTO != null && !sDeviceDTO.getValid().equals("0")) {
            /*信号强度*/
            String Q = null;
            if (rawdataMap.containsKey("Q")){
                Q = rawdataMap.get("Q").toString();
            }

            /*电量信息*/
            Double V = null;
            if (rawdataMap.containsKey("V")){
                V = Double.valueOf(rawdataMap.get("V").toString())/1000;
            }

            /*更新设备表里的电量和信号*/
            sDeviceService.update(sDeviceMapper.toEntity(sDeviceDTO));

            /*获取检测项数据*/
            SDevicetypeDTO type = sDevicetypeService.findById(Long.valueOf(sDeviceDTO.getDevicetype()));
            String[] key = type.getCode().split(",");
            String[] gs = type.getGs().split(",");
            Long time =System.currentTimeMillis();
            /*循环遍历当前设备检测项code,例如：['TMP','HR','O2']*/
            for(int i = 0; i<key.length; i++){
                List<SData> datalst = new ArrayList<>();
                if (rawdataMap.containsKey(key[i])) {
                    //获取上传数据中的检测数据value
                    List<Integer> value = (List<Integer>) rawdataMap.get(key[i]);
                    //如果上传数据不为空，开始处理数据
                    if (value != null && !value.isEmpty()) {
                        for (int j = value.size() -1 ; j >= 0; j--){
                            SData sData = new SData();
                            sData.setItem(key[i]);
                            sData.setDeviceid(U);
                            //执行公式
                            String zxgs = gs[i].replace("@data", value.get(j).toString());

                            //湿度单独处理，防止超出100
                            if (key[i].equals("HR") || key[i].contains("sHR")){
                                try {
                                    if (Double.valueOf(jse.eval(zxgs).toString()) > 100) {
                                        sData.setData(String.valueOf(100));
                                    }else {
                                        sData.setData(String.valueOf(jse.eval(zxgs)));
                                    }
                                } catch (ScriptException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    sData.setData(String.valueOf(jse.eval(zxgs)));
                                } catch (ScriptException e) {
                                    e.printStackTrace();
                                }
                            }

                            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            if (j == value.size() -1) {
                                sData.setDtime(dateformat.format(time));
                            }else {
                                int oldTime = (value.size()-1-j) * Integer.valueOf(rawdataMap.get("t1").toString());
                                sData.setDtime(dateformat.format(time-oldTime));
                            }
                            if (Q != null) {
                                sData.setRssi(Q);
                            }

                            if (V != null) {
                                sData.setBattery(String.valueOf(V));
                            }
                            log.debug("处理后的数据:{}",sData);
                            if (j == value.size() - 1 ) {
//                                warnMsg(sData);
                                Thread t = new Thread(new Runnable(){
                                    @SneakyThrows
                                    public void run(){
                                        // 起线程执行报警过滤
                                        warnMsg(sData);
                                    }});
                                t.start();
                            }
                            datalst.add(sData);
                        }
                        //存储数据到mongoDB
                        log.info("要存储的数据列表:{}", datalst);
                        sDataService.insertData(datalst);
                    }else {
                        log.debug("设备{}的检测项为:{},检测项{}没有数据", U,key,key[i]);
                    }
                }else {
                    log.debug("设备{}的检测项为:{},其中当前数据没有'{}'的数据", U,key,key[i]);
                }

            }
        }
    }
    @Log("查询getData")
    @ApiOperation(value = "查询getData")
    @PostMapping(value = "/getData")
    public ResponseEntity getData(@Validated @RequestBody Map map, Pageable pageable){
        String deviceid = (String)map.get("deviceid");
        String item = (String)map.get("item");
        String starttime = (String)map.get("starttime");
        String endtime = (String)map.get("endtime");
//        Map<String,Object> dataMap = sDataService.queryAll(deviceid,item,starttime,endtime);
        Query query = new Query();
        Query queryNopage = new Query();
        query.addCriteria(Criteria.where("deviceid").is(deviceid));
        query.addCriteria(Criteria.where("item").is(item));
        query.addCriteria(Criteria.where("dtime").gte(starttime).lte(endtime));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "dtime")));
        queryNopage.addCriteria(Criteria.where("deviceid").is(deviceid));
        queryNopage.addCriteria(Criteria.where("item").is(item));
        queryNopage.addCriteria(Criteria.where("dtime").gte(starttime).lte(endtime));
        queryNopage.with(new Sort(new Sort.Order(Sort.Direction.ASC, "dtime")));
//        System.out.println(pageable.getPageNumber()+" 1111111111111111111 "+pageable.getPageSize());
        int page = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        query.skip((page-1)*pageSize);
        query.limit(pageSize);
        List<SData> dataList = mongoTemplate.find(query, SData.class);
        List<SData> dataListNopage = mongoTemplate.find(queryNopage, SData.class);
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("data", dataList);
        dataMap.put("dataNopage", dataListNopage);
        dataMap.put("total", dataListNopage.size());
        return new ResponseEntity(dataMap,HttpStatus.CREATED);
    }

    @Log("查询getIllData")
    @ApiOperation(value = "查询getIllData")
    @PostMapping(value = "/getIllData")
    public ResponseEntity getIllData(@Validated @RequestBody Map map, Pageable pageable) throws Exception {
        String deviceid = (String)map.get("deviceid");
        String item = (String)map.get("item");
        String start = (String)map.get("starttime");
        String starttime = start + " 00:00:00";
        String end = (String)map.get("endtime");
        String endtime = end + " 23:59:59";
        if(!item.contains("ILL")){
            log.error("请选择光照时长检测项！");
            Map<String,String>map1 = new HashMap<>();
            map1.put("message","请选择光照时长检测项！");
            return new ResponseEntity(map1 ,HttpStatus.BAD_REQUEST);
        }
        SDeviceDTO sDeviceDTO = sDeviceService.findByDeviceid(deviceid);
        Query queryNopage = new Query();
        queryNopage.addCriteria(Criteria.where("deviceid").is(deviceid));
        queryNopage.addCriteria(Criteria.where("item").is(item));
        queryNopage.addCriteria(Criteria.where("dtime").gte(starttime).lte(endtime));
        queryNopage.with(new Sort(new Sort.Order(Sort.Direction.ASC, "dtime")));
        List<SData> dataListNopage = mongoTemplate.find(queryNopage, SData.class);
        List<SData> illmap = new ArrayList<>();
        String[] days = null;
        Double[] illdata = null;
        if(dataListNopage.size()>0){// 有数据
            // 计算相差天数 start
            Date date1 = DateUtil.parse(start);
            Date date2 = DateUtil.parse(end);
            //相差天数
            long betweenDay = DateUtil.between(date1, date2, DateUnit.DAY);
            betweenDay = betweenDay + 1;
            days = new String[(int) betweenDay];
            illdata = new Double[(int) betweenDay];
            String day =end;
            for(int i=(int)betweenDay-1; i>=0; i--){
                days[i] = day;
                day = getYestoday(day);
            }
            // 计算相差天数 end days
            String nt = sDeviceDTO.getNt();
            int timetrack = sDeviceDTO.getTimetrack();
            if(StringUtil.strIsNullOrEmpty(nt)){
                log.error("本设备没有设置光照时长统计阈值！");
                Map<String,String>map1 = new HashMap<>();
                map1.put("message","本设备没有设置光照时长统计阈值！！");
                return new ResponseEntity(map1 ,HttpStatus.BAD_REQUEST);
            }
            for(int j=0;j<days.length;j++){
                Double illtime = Double.valueOf(0);
                SData sData = new SData();
                for(int i=0;i<dataListNopage.size();i++){
                    String jday = days[j];
                    String iday = dataListNopage.get(i).getDtime().substring(0,10);
                    if(jday.equals(iday)){
                        String data = dataListNopage.get(i).getData();
                        if(StringUtil.strIsNullOrEmpty(data)){
                            continue;
                        }
                        if(Double.parseDouble(data) >= Double.parseDouble(nt)){
                            illtime = illtime + timetrack/(1000*60);//转为分钟
                        }
                    }
                }
                sData.setDeviceid(deviceid);
                sData.setDtime(days[j]);
                sData.setData(illtime.toString());
                illdata[j] = illtime;
                illmap.add(sData);
            }
        }
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("days", days);
        dataMap.put("illdata", illdata);
        dataMap.put("illmap", illmap);
        return new ResponseEntity(dataMap,HttpStatus.CREATED);
    }

    @Log("导出exportExcel")
    @ApiOperation(value = "导出exportExcel")
    @PostMapping(value = "/exportExcel")
    public ResponseEntity exportExcel(@Validated @RequestBody Map map, HttpServletResponse response){
        String deviceid = (String)map.get("deviceid");
        String item = (String)map.get("item");
        String starttime = (String)map.get("starttime");
        String endtime = (String)map.get("endtime");
//        Map<String,Object> dataMap = sDataService.queryAll(deviceid,item,starttime,endtime);
        try {
            String fileName="data.xlsx";
            response.setContentType("application/excel");
            response.setHeader("Content-disposition","attachment;filename=" +fileName +";filename*=utf-8''"+ URLEncoder.encode(fileName,"UTF-8"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 生成EXCEL并指定输出路径
            //OutputStream out = new FileOutputStream(appRoot+"\\data.xlsx");
            OutputStream out = response.getOutputStream();
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            // 设置SHEET
            Sheet sheet = new Sheet(1, 0);
            sheet.setSheetName("sheet1");

            // 设置标题
            Table table = new Table(1);
            List<List<String>> titles = new ArrayList<List<String>>();
            titles.add(Arrays.asList("数值"));
            titles.add(Arrays.asList("更新时间"));
            table.setHead(titles);
            // 查询数据
            Query queryNopage = new Query();
            queryNopage.addCriteria(Criteria.where("deviceid").is(deviceid));
            queryNopage.addCriteria(Criteria.where("item").is(item));
            queryNopage.addCriteria(Criteria.where("dtime").gte(starttime).lte(endtime));
            queryNopage.with(new Sort(new Sort.Order(Sort.Direction.ASC, "dtime")));
            List<SData> lst = mongoTemplate.find(queryNopage, SData.class);
            if(lst.size()==0){
                log.error("exportExcel 导出excel,  没有数据！");
                return ResponseEntity.badRequest().body("没有数据!");
            }
            List<List<String>> dataList = new ArrayList<>();
            for (int i = 0; i < lst.size(); i++) {
                String data = lst.get(i).getData();
                String dtime =lst.get(i).getDtime();
//                Date date = new Date(adtime);
//                String dtime = sdf.format(date);
                dataList.add(Arrays.asList(data,dtime));
            }
            writer.write0(dataList, sheet, table);
            writer.finish();
            //mdataService.exportExcel(deviceid, item, starttime, endtime, response);
            log.debug("exportExcel 导出excel, end 导出成功！");
            return ResponseEntity.status(HttpStatus.OK).body("success");
        }catch (Exception e){
            e.printStackTrace();
            log.error("exportExcel 导出excel, end 导出失败！");
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("exportExcel 导出excel失败!");
        }
    }

    @Log("查询loadCDataByDeviceid")
    @ApiOperation(value = "查询loadCDataByDeviceid")
    @PostMapping(value = "/loadCDataByDeviceid")
    public ResponseEntity loadCDataByDeviceid(@Validated @RequestBody Map map){
        String deviceid = (String)map.get("deviceid");
//        Map<String,Object> dataMap = sDataService.queryAll(deviceid,item,starttime,endtime);
        Query query = new Query();
        //1、获取设备信息
        SDeviceDTO sDeviceDTO = null;
        if(deviceid == null || deviceid.length()==0){
            JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
            String userid = jwtUser.getId().toString();
            sDeviceDTO =  sDeviceService.loadOneDevice(userid);
            if(sDeviceDTO==null){
                return ResponseEntity.badRequest().body("没有设备!");
            }
            deviceid = sDeviceDTO.getDeviceid();
        }else{
            sDeviceDTO =  sDeviceService.findByDeviceid(deviceid);
        }
        //2、获取设备数据
        String endtime = DateUtil.now();
        DateTime start = DateUtil.yesterday();
        String starttime = DateUtil.formatDateTime(start);
//        String starttime = "2019-10-23 00:00:00";
//        String endtime = "2019-10-23 23:59:59";
        query.addCriteria(Criteria.where("deviceid").is(deviceid));
        query.addCriteria(Criteria.where("dtime").gte(starttime).lte(endtime));
        List<SData> dataList = mongoTemplate.find(query, SData.class);
        Map<String,Object> returnMap = new HashMap<String,Object>();
        Map<String,Object> dataMap =
                (Map) dataList.stream().collect(groupingBy(SData::getItem));
        //3。获取设备对应的项目信息
        SProjectDeviceDTO sProjectDeviceDTO = sProjectDeviceService.findByDeviceid(deviceid);
        String projectid = sProjectDeviceDTO.getProjectid();
        SProjectDTO sProjectDTO = sProjectService.findById(Long.parseLong(projectid));
        returnMap.put("dataMap",dataMap);
        returnMap.put("sDeviceDTO",sDeviceDTO);
        returnMap.put("sProjectDTO",sProjectDTO);
        return new ResponseEntity(returnMap,HttpStatus.OK);
    }

    @Log("查询getDataByDevice")
    @ApiOperation(value = "查询getDataByDevice")
    @GetMapping(value = "/getDataByDevice")
    public ResponseEntity getDataByDevice(String deviceId, String start, String end){
        Date starttime = DateUtil.parse(start);
        Date endtime = DateUtil.parse(end);
        long betweenDay = DateUtil.between(starttime, endtime, DateUnit.DAY);
        if (betweenDay > 7) {
//            return R.error("查询时间跨度不能大于7天");
            Map<String,String> map = new HashMap<>();
            map.put("message","查询时间跨度不能大于7天！");
            log.info("getDataByDevice:查询时间跨度不能大于7天！");
            return new ResponseEntity(map , HttpStatus.BAD_REQUEST);
        }
        SDeviceDTO sDeviceDTO = sDeviceService.findByDeviceid(deviceId);
        if (sDeviceDTO != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("deviceid").is(deviceId));
            query.addCriteria(Criteria.where("dtime").gte(start).lte(end));
            List<SData> dataList = mongoTemplate.find(query, SData.class);
            return new ResponseEntity(dataList,HttpStatus.OK);
        } else {
//            return R.error("没有该设备");
            Map<String,String> map = new HashMap<>();
            map.put("message","没有该设备");
            log.info("getDataByDevice:没有该设备");
            return new ResponseEntity(map , HttpStatus.BAD_REQUEST);
        }
    }

    @Log("查询loadCDataByProjectid")
    @ApiOperation(value = "查询loadCDataByProjectid")
    @GetMapping(value = "/loadCDataByProjectid")
    public ResponseEntity loadCDataByProjectid(String projectid){
//        String projectid = (String)map.get("projectid");
        List<SDeviceDTO> list = sDeviceService.getDeviceByProjectid(Long.parseLong(projectid));
        Map<String,Object> dataMap = new HashMap<>();
        List<List<SData>> datalist = new ArrayList<List<SData>>();
        List<SDeviceDTO> devicelist = new ArrayList<SDeviceDTO>();
        for(int i=0;i<list.size();i++){
            String deviceid = list.get(i).getDeviceid();
            List<SData> data = getNowDataBydeviceid(deviceid);
            datalist.add(data);
        }
        dataMap.put("data",datalist);
        dataMap.put("device",devicelist);
        return new ResponseEntity(dataMap,HttpStatus.OK);
    }

    @Log("查询loadNowDataByUserid")
    @ApiOperation(value = "查询loadNowDataByUserid")
    @GetMapping(value = "/loadNowDataByUserid")
    public ResponseEntity loadNowDataByUserid(){
        JwtUser jwtUser = (JwtUser)userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        String userid = jwtUser.getId().toString();
        List<SDeviceDTO> list = sDeviceService.queryAllByUseridNoPage(userid);
        List<SData> dataAll = new ArrayList<SData>();
        for(int i=0;i<list.size();i++){
            String deviceid = list.get(i).getDeviceid();
            List<SData> data = getNowDataBydeviceid(deviceid);
            dataAll.addAll(data);
        }
        return new ResponseEntity(dataAll,HttpStatus.OK);
    }

    @Log("查询loadNowDataByDeviceid")
    @ApiOperation(value = "查询loadNowDataByDeviceid")
    @GetMapping(value = "/loadNowDataByDeviceid/{deviceid}")
    public ResponseEntity loadNowDataByDeviceid(@PathVariable String deviceid){
        List<SData> data = getNowDataBydeviceid(deviceid);
        return new ResponseEntity(data,HttpStatus.OK);
    }

    /**
     * 通过设备id获取实时数据
     * @param deviceid
     * @return
     */
    public List<SData> getNowDataBydeviceid(String deviceid){
        SDeviceDTO sDevice = sDeviceService.findByDeviceid(deviceid);
        String deviceType = sDevice.getDevicetype();
        SDevicetypeDTO sDevicetypeDTO = sDevicetypeService.findById(Long.parseLong(deviceType));
        List<SData> data = new ArrayList<SData>();
        if(sDevicetypeDTO != null){
            String codes = sDevicetypeDTO.getCode();
            String[] code = codes.split(",");
            // Arrays.sort(code); 数组排序
            for(int j=0;j<code.length;j++) {
                Query query = new Query();
                query.addCriteria(Criteria.where("deviceid").is(deviceid));
                query.addCriteria(Criteria.where("item").is(code[j]));
                query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "dtime")));
                SData sData = mongoTemplate.findOne(query, SData.class);
                if(sData != null){
                    data.add(sData);
                }
            }
        }else{
            log.error("设备类型不存在："+deviceType);
            throw new BadRequestException("设备类型不存在！"+deviceType);
        }
        return data;
    }

    public void warnMsg(SData sData) throws InterruptedException {
        //step1: 查询报警设置表中设备的item 报警设置
        SWarnDTO sWarn = sWarnService.getWarn(sData.getDeviceid(), sData.getItem());
        if (sWarn != null) {
            Boolean w = false;
            //step2: 判断接收数据是否满足报警条件
            if (sWarn.getWarntype().equals("1")) { //数值型报警
                // 符合报警条件
                BigDecimal min = sWarn.getMin();
                if(min != null){
                    if (Double.valueOf(sData.getData()) <= sWarn.getMin().doubleValue()) {
                        // 保存报警信息+发送报警信息给用户
                        saveWarnMsgAndSendMsg(sData, sWarn);
                    }
                }
                BigDecimal max = sWarn.getMax();
                if(max!=null){
                    if (Double.valueOf(sData.getData()) >= sWarn.getMax().doubleValue()) {
                        // 保存报警信息+发送报警信息给用户
                        saveWarnMsgAndSendMsg(sData, sWarn);
                    }
                }
            }else if(sWarn.getWarntype().equals("0")) { //开关型报警
                if (sData.getData().equals(sWarn.getState())) {
                    // 保存报警信息+发送报警信息给用户
                    saveWarnMsgAndSendMsg(sData, sWarn);
                }
            }else { //控制器联动（智能控制）
                // 符合报警条件
                BigDecimal min = sWarn.getMin();
                if(min != null){
                    if (Double.valueOf(sData.getData()) <= sWarn.getMin().doubleValue()) { //接收到的值 小于等于设置的最小值
                        // 保存报警信息+发送报警信息给用户
                        saveWarnMsgAndSendMsg(sData, sWarn);
                        //智能控制
                        String flag = "min";
                        smartControlWay(sData,sWarn,flag);
                    }
                }
                BigDecimal max = sWarn.getMax();
                if(max!=null){
                    if(Double.valueOf(sData.getData()) >= sWarn.getMax().doubleValue()){ //接收到的值 大于等于设置的最大值
                        // 保存报警信息+发送报警信息给用户
                        saveWarnMsgAndSendMsg(sData, sWarn);
                        //智能控制
                        String flag = "max";
                        smartControlWay(sData,sWarn,flag);
                    }
                }
            }
        }
    }

    private void smartControlWay(SData sData, SWarnDTO sWarn, String flag) throws InterruptedException {
        String warnid = sWarn.getId().toString();
        log.info(MessageFormat.format("warnid， flag：{0} {1}", warnid, flag));
        List<SWarnWayDTO> list = sWarnWayService.findAllByWarnidAndFlagAndValid(warnid, flag);
        List<SWarnWayDTO> cangmaList = new ArrayList<>();
        List<SWarnWayDTO> elseList = new ArrayList<>();
        for (SWarnWayDTO sWarnWayDTO : list) {
            String wayname = sWarnWayDTO.getWayname();
            String way = sWarnWayDTO.getWay();
            String status = sWarnWayDTO.getStatus();
            String deviceid = sWarnWayDTO.getDeviceid();
            String[] plcArr = cangmaPLC.split(",");
            List<String> plcList = Arrays.asList(plcArr);
            if (plcList.contains(deviceid)) {
                Thread.sleep(2000L);
                log.info("================延迟两秒，开始执行下一条指令===================");
                cangmaList.add(sWarnWayDTO);
                Boolean flag1 = false;
                String deviceidWay = deviceid+way;
                //获取item对应的寄存器地址
                cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(ControlPLCWayConstants.WAY_TO_ADDRESS);
                String register = String.valueOf(jsonObject.get(deviceidWay)); //寄存器地址
                //      地址 个数 字节 数据  CRC
                // 0110 A080 0001  02  0001
                String cmd = "0110";
                String hexVale =  status;
                if(register.length()>4){
                    cmd = cmd + register.substring(0,4);
                    String cmmd = register.substring(4);
                    if("1".equals(status)){
                        flag1 = true;
                        hexVale = cmmd;
                    }
                }else{
                    if (wayname.contains("风机")) {
                        flag1 = true;
                    }
                    cmd = cmd + register;
                }
                if (hexVale.length() < 4) {
                    hexVale = StringUtils.leftPad(hexVale, 4, "0").toUpperCase();
                }
                log.info("hexVale:{}", hexVale);
                String closeCmd = cmd +"000102" + "0000";
                cmd = cmd +"000102" + hexVale;
                List<SWarnLog> warnLogList = sWarnLogRepository.findAllByCommondEqualsAndMsgEqualsAndCreateTimeGreaterThan(cmd, "成功", new Timestamp(System.currentTimeMillis() - 90000L));
                if(flag1 && warnLogList.size() == 0){
                    log.info("开启正转反转之前调用关闭 向{}控制器发送指令:{}",deviceid,closeCmd);
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("relayId", deviceid);
                    paramMap.put("command", closeCmd);
                    String url = address;//8007
                    ResponseEntity responseEntity =getHttpRequeat(sWarn, sData, sWarnWayDTO, paramMap, url);
                    responseEntity.getStatusCode();
                    responseEntity.getBody().toString();
                    log.info("开启正转反转之前调用关闭 请求地址："+responseEntity.getBody().toString());
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Thread.sleep(2000L);
                }
                log.info("向{}控制器发送指令:{}",deviceid,cmd);
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("relayId", deviceid);
                paramMap.put("command", cmd);
                String url = address;//8007
                log.info("请求地址："+url);
                getHttpRequeat(sWarn, sData, sWarnWayDTO, paramMap, url);
            } else {
                elseList.add(sWarnWayDTO);
            }
        }

        if (elseList.size() != 0) {
            Map<String, String> cmdmap = getCmdAndWayinfo(list);
            String commond = cmdmap.get("cmd");
            String wayinfo = cmdmap.get("wayinfo");
            String deviceid = "";
            if ("min".equals(flag)) {
                deviceid = sWarn.getRelayidmin();
            } else {
                deviceid = sWarn.getRelayidmax();
            }
            //调用控制方法 controlWay
            String result = sDeviceWayService.controlWay(deviceid, commond);
//        List<SDeviceWayDTO> list = sDeviceWayService.findByDeviceid(deviceid);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("relayId", deviceid);
            paramMap.put("command", commond);
            createSWarnLog(sWarn, result, paramMap, sData, wayinfo);
        }
    }

    private ResponseEntity getHttpRequeat(SWarnDTO sWarn, SData sData, SWarnWayDTO resources, Map<String, Object> paramMap, String url) {
        String cmd = "";
        String wayinfo = "";
        if("1".equals(resources.getValid())){
            cmd = resources.getStatus() + cmd;
            String status = resources.getStatus();
            if("0".equals(status)){
                wayinfo = "关"+wayinfo;
            }else{
                wayinfo = "开"+wayinfo;
            }
        }else{
            cmd = "X" + cmd;
            wayinfo = "空"+wayinfo;
        }
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
                sWarnWayService.update(sWarnWayMapper.toEntity(resources));
                //2、保存操作记录
                //SWarnDTO sWarn, String message,Map<String, Object> paramMap,SData sData,String wayinfo)
                String message = "成功";
                createSWarnLog(sWarn, message, paramMap, sData, wayinfo);
                log.info("getHttpRequeat:"+message+" "+result);
                return ResponseEntity.status(200).body("success");
            }else if("fail".equals(result)){
                //1、保存操作记录
                String message = "发送失败";
                createSWarnLog(sWarn, message, paramMap, sData, wayinfo);
                Map<String,String>map = new HashMap<>();
                map.put("message","发送失败！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map , HttpStatus.BAD_REQUEST);
            }else if("exefail".equals(result)){
                //1、保存操作记录
                String message = "执行失败";
                createSWarnLog(sWarn, message, paramMap, sData, wayinfo);
                Map<String,String>map = new HashMap<>();
                map.put("message","执行失败！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }else if("timeout".equals(result)){
                //1、保存操作记录
                String message = "超时";
                createSWarnLog(sWarn, message, paramMap, sData, wayinfo);
                Map<String,String>map = new HashMap<>();
                map.put("message","超时失败！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }else if("offline".equals(result)){
                //保存任务执行记录
                String message = "设备离线";
                createSWarnLog(sWarn, message, paramMap, sData, wayinfo);
                Map<String,String>map = new HashMap<>();
                map.put("message","设备离线！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }else{
                String message = "请求错误";
                createSWarnLog(sWarn, message, paramMap, sData, wayinfo);
                Map<String,String>map = new HashMap<>();
                map.put("message","请求错误！");
                log.info("getHttpRequeat:"+message+" "+result);
                return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("SDeviceWayController->controlWay->catch:"+e.getMessage());
            String message = "请求失败";
            createSWarnLog(sWarn, message, paramMap, sData, wayinfo);
            Map<String,String>map = new HashMap<>();
            map.put("message","请求失败！");
            return new ResponseEntity(map ,HttpStatus.BAD_REQUEST);
        }
    }

    /*public void controlWay(SWarnDTO sWarn,String deviceid,String commond,String flag,SData sData,String wayinfo){
        log.info("warnid:"+sWarn.getId().toString()+" deviceid:"+deviceid +" commond:"+commond+" flag:"+flag);
//        String deviceid = str.split("-")[0];
//        String commond = str.split("-")[1];
//        String taskid = str.split("-")[2];
        SDeviceDTO sDeviceDTO = sDeviceService.findByDeviceid(deviceid);
        String devicetype = sDeviceDTO.getDevicetype();
        // 202,网络继电器
        if("202".equals(devicetype)){
            // 举例 X1
            StringBuffer sb = new StringBuffer(commond);
            // 反转 1X
            String comesc = sb.reverse().toString();
            List<SDeviceWayDTO> list = sDeviceWayService.findByDeviceid(deviceid);
            for(int i=0;i<list.size();i++){
                String status = String.valueOf(comesc.charAt(i));
                if(!"X".equals(status)){
                    // 修改支路状态，稍后保存到数据库
                    list.get(i).setStatus(status);
                }
            }
            for(int i=1;i<=comesc.length();i++){
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("relayId", deviceid);
                paramMap.put("way", i);
                String status = comesc.charAt(i-1)+"";
                paramMap.put("status", status);
                String url = addressNR;//8007
                log.info("202请求地址："+url+" paramMap:"+paramMap.toString());
                if(!"X".equals(status)){
                    getHttpRequestWay(sWarn, list, paramMap, url, flag, sData, wayinfo);
                }
            }
        } else {
            // 倒序 87654321
            // 举例 010XX011
            StringBuffer sb = new StringBuffer(commond);
            // 反转 110XX010
            String comesc = sb.reverse().toString();
            StringBuilder comstr = new StringBuilder(comesc);
            List<SDeviceWayDTO> list = sDeviceWayService.findByDeviceid(deviceid);
            // 当前各支路状态 倒序组合
            String stausstr = "";
            for(int i=0;i<list.size();i++){
                String statusdb = list.get(i).getStatus();
                stausstr = statusdb + stausstr;
                String status = String.valueOf(comesc.charAt(i));
                if("X".equals(status)){
                    // 替换X为当前数据库支路状态
                    comstr.replace(i,i+1,statusdb);
                }else{
                    // 修改支路状态，稍后保存到数据库
                    list.get(i).setStatus(status);
                }
            }
            log.info("comstr:"+comstr);
            StringBuffer sb1 = new StringBuffer(comstr);
            String com = sb1.reverse().toString();
            //4、位数补足16位             如：        00000101
            String sendStatus = String.format("%16s", com);
            //5、空格替换为0              如：0000000000000101
            sendStatus = sendStatus.replaceAll("\\s", "0");
            log.info("sendStatus:"+sendStatus);
            // 执行控制指令
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("relayId", deviceid);
            paramMap.put("command", sendStatus);
            String url = address;//8007
            log.info("relayId:"+deviceid+" command:"+sendStatus);
            log.info("控制器请求地址："+url);
            getHttpRequestWay(sWarn, list, paramMap, url, flag, sData, wayinfo);
        }
    }*/
    /*private void getHttpRequestWay(SWarnDTO sWarn, List<SDeviceWayDTO> list, Map<String, Object> paramMap, String url ,String flag,SData sData,String wayinfo) {
        try {
            String result = HttpUtil.get(url, paramMap);
            log.info("ControlWayTask->controlWayByTask->result:"+result);
            if("success".equals(result)){
                // 支路新状态保存数据库
                for(int i = 0;i<list.size();i++){
                    sDeviceWayService.update(sDeviceWayMapper.toEntity(list.get(i)));
                }
                //保存任务执行记录
                String message = "成功";
                createSWarnLog(sWarn, message, flag, paramMap, sData, wayinfo);
                log.info(message);
            }else if("fail".equals(result)){
                //保存任务执行记录
                String message = "发送失败";
                createSWarnLog(sWarn, message, flag, paramMap, sData, wayinfo);
                log.info(message);
            }else if("exefail".equals(result)){
                //保存任务执行记录
                String message = "执行失败";
                createSWarnLog(sWarn, message, flag, paramMap, sData, wayinfo);
                log.info(message);
            }else if("timeout".equals(result)){
                //保存任务执行记录
                String message = "超时";
                createSWarnLog(sWarn, message, flag, paramMap, sData, wayinfo);
                log.info(message);
            }else{
                //保存任务执行记录
                String message = "请求错误";
                createSWarnLog(sWarn, message, flag, paramMap, sData, wayinfo);
                log.info(message);
            }
        }catch (Exception e){
            e.printStackTrace();
            //保存任务执行记录
            String message = "请求失败";
            createSWarnLog(sWarn, message, flag, paramMap, sData, wayinfo);
            log.info(message);
        }
    }*/
    private void createSWarnLog(SWarnDTO sWarn, String message, Map<String, Object> paramMap,SData sData,String wayinfo) {
        SWarnLog sWarnLog = new SWarnLog();
        sWarnLog.setWarnid(sWarn.getId().toString());
        sWarnLog.setWarntype(sWarn.getWarntype());
        sWarnLog.setItem(sWarn.getItem());
        sWarnLog.setWarntime(Timestamp.valueOf(sData.getDtime()));
        sWarnLog.setWayinfo(wayinfo);
        sWarnLog.setWarnvalue(sData.getData());
        sWarnLog.setDeviceid(sWarn.getDeviceid());
        sWarnLog.setRelayid((String)paramMap.get("relayId"));
        sWarnLog.setCommond((String)paramMap.get("command"));
        sWarnLog.setMsg(message);
        sWarnLogService.create(sWarnLog);
    }

    private Map<String,String> getCmdAndWayinfo(List<SWarnWayDTO> list){
        String cmd = "";
        String wayinfo = "";
        for(int i=0;i<list.size();i++){
            SWarnWayDTO sWarnWayDTO = list.get(i);
//            String devwayjson = new JSONObject().toJSONString(deviceWayMap);
//            STaskWay sDeviceWay = new JSONObject().parseObject(devwayjson, STaskWay.class);
            if("1".equals(sWarnWayDTO.getValid())){
                cmd = sWarnWayDTO.getStatus() + cmd;
                String status = sWarnWayDTO.getStatus();
                if("0".equals(status)){
                    wayinfo = (i+1)+"关"+wayinfo;
                }else{
                    wayinfo = (i+1)+"开"+wayinfo;
                }
            }else{
                cmd = "X" + cmd;
                wayinfo = (i+1)+"空"+wayinfo;
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("cmd",cmd);
        map.put("wayinfo",wayinfo);
        return map;
    }

    /**
     * 保存报警信息+发送报警信息给用户
     * @param sData
     * @param sWarn
     */
    private void saveWarnMsgAndSendMsg(SData sData, SWarnDTO sWarn) {
        // 查询flg='0'（状态为报警）的报警信息
        SWarnmsgDTO sWarnmsgDTO = sWarnmsgService.queryByDeviceIdAndItem(sData.getDeviceid(), sData.getItem());
        //设备最后一次报警信息 距离当前时间 大于30分钟报警
        if (sWarnmsgDTO == null || DateUtil.between(new Date(), sWarnmsgDTO.getDtime(), DateUnit.MINUTE) > 30) {
            //保存报警记录
            SWarnmsg sWarnmsg = new SWarnmsg();
            sWarnmsg.setDeviceid(sData.getDeviceid());
            sWarnmsg.setItem(sData.getItem());
            sWarnmsg.setFlg("0");
            sWarnmsg.setMessage(sWarn.getMsg());
            sWarnmsgService.create(sWarnmsg);
            //获取设备关联的用户
            List<User> users = sDeviceService.getUserByDeviceid(sData.getDeviceid());
            //发送短信报警
            for (User user : users) {
                SDKTestSendTemplateSMS.sendMsg(user.getPhone(), "487466", new String[]{sData.getDeviceid(), sWarn.getMsg()});
            }
        }
    }

    private String getYestoday(@RequestParam(value = "startTimeStr", required = false, defaultValue = "") String startTimeStr) throws ParseException {
        //date 日期-1天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date sDate = sdf.parse(startTimeStr);
        Calendar c = Calendar.getInstance();
        c.setTime(sDate);
        c.add(Calendar.DAY_OF_MONTH, -1);
        sDate = c.getTime();
        return sdf.format(sDate);
    }
}