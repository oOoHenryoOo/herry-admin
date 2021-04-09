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

import me.zhengjie.modules.smart.domain.DeviceParam;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.smart.repository.DeviceParamRepository;
import me.zhengjie.modules.smart.service.DeviceParamService;
import me.zhengjie.modules.smart.service.dto.DeviceParamDto;
import me.zhengjie.modules.smart.service.dto.DeviceParamQueryCriteria;
import me.zhengjie.modules.smart.service.mapstruct.DeviceParamMapper;
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
* @date 2021-02-26
**/
@Service
@RequiredArgsConstructor
public class DeviceParamServiceImpl implements DeviceParamService {

    private final DeviceParamRepository deviceParamRepository;
    private final DeviceParamMapper deviceParamMapper;

    @Override
    public Map<String,Object> queryAll(DeviceParamQueryCriteria criteria, Pageable pageable){
        Page<DeviceParam> page = deviceParamRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(deviceParamMapper::toDto));
    }

    @Override
    public List<DeviceParamDto> queryAll(DeviceParamQueryCriteria criteria){
        return deviceParamMapper.toDto(deviceParamRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DeviceParamDto findById(Long id) {
        DeviceParam deviceParam = deviceParamRepository.findById(id).orElseGet(DeviceParam::new);
        ValidationUtil.isNull(deviceParam.getId(),"DeviceParam","id",id);
        return deviceParamMapper.toDto(deviceParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceParamDto create(DeviceParam resources) {
        return deviceParamMapper.toDto(deviceParamRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeviceParam resources) {
        DeviceParam deviceParam = deviceParamRepository.findById(resources.getId()).orElseGet(DeviceParam::new);
        ValidationUtil.isNull( deviceParam.getId(),"DeviceParam","id",resources.getId());
        deviceParam.copy(resources);
        deviceParamRepository.save(deviceParam);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            deviceParamRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DeviceParamDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeviceParamDto deviceParam : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("传感器id 唯一设备号", deviceParam.getDeviceid());
            map.put("模式0:自动模式1:手动模式", deviceParam.getDmode());
            map.put("设定手动模式下的开关状态", deviceParam.getDstatus());
            map.put("开灯时间", deviceParam.getOpentime());
            map.put("关灯时间", deviceParam.getClosetime());
            map.put("拍照间隔", deviceParam.getPhotointerval());
            map.put("烘干时间", deviceParam.getDrytime());
            map.put("创建时间", deviceParam.getCreateTime());
            map.put("更新时间", deviceParam.getUpdateTime());
            map.put("备注", deviceParam.getNt());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}