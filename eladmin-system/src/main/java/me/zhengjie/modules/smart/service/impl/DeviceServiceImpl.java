/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.modules.smart.service.impl;

import me.zhengjie.modules.smart.domain.Device;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.smart.repository.DeviceRepository;
import me.zhengjie.modules.smart.service.DeviceService;
import me.zhengjie.modules.smart.service.dto.DeviceDto;
import me.zhengjie.modules.smart.service.dto.DeviceQueryCriteria;
import me.zhengjie.modules.smart.service.mapstruct.DeviceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author lhl
* @date 2021-04-28
**/
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    @Override
    public Map<String,Object> queryAll(DeviceQueryCriteria criteria, Pageable pageable){
        Page<Device> page = deviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(deviceMapper::toDto));
    }

    @Override
    public List<DeviceDto> queryAll(DeviceQueryCriteria criteria){
        return deviceMapper.toDto(deviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DeviceDto findById(Long id) {
        Device device = deviceRepository.findById(id).orElseGet(Device::new);
        ValidationUtil.isNull(device.getId(),"Device","id",id);
        return deviceMapper.toDto(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceDto create(Device resources) {
        return deviceMapper.toDto(deviceRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Device resources) {
        Device device = deviceRepository.findById(resources.getId()).orElseGet(Device::new);
        ValidationUtil.isNull( device.getId(),"Device","id",resources.getId());
        device.copy(resources);
        deviceRepository.save(device);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            deviceRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DeviceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeviceDto device : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("所属公司", device.getCompany());
            map.put("IMEI码", device.getImei());
            map.put("设备id", device.getDeviceid());
            map.put("监测设备类型", device.getDevicetype());
            map.put("设备名称", device.getName());
            map.put("描述", device.getNt());
            map.put("设备类型 0:nb 1:4g", device.getType());
            map.put("安装位置", device.getAddress());
            map.put("经度", device.getJd());
            map.put("纬度", device.getWd());
            map.put("采集频率", device.getTimetrack());
            map.put("上传频率", device.getTimetrack2());
            map.put("是否有效", device.getValid());
            map.put("ip", device.getIp());
            map.put("端口", device.getPort());
            map.put("二维码", device.getQrcode());
            map.put("创建时间", device.getCreateTime());
            map.put("更新时间", device.getUpdateTime());
            map.put("工作模式 0手动1自动", device.getWorkmode());
            map.put("在线时间", device.getOnlinetime());
            map.put("在线", device.getOnline());
            map.put("到期时间", device.getExpire());
            map.put("运营商", device.getIsp());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}