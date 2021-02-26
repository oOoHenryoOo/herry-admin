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

import me.zhengjie.modules.smart.domain.DevicePhoto;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.smart.repository.DevicePhotoRepository;
import me.zhengjie.modules.smart.service.DevicePhotoService;
import me.zhengjie.modules.smart.service.dto.DevicePhotoDto;
import me.zhengjie.modules.smart.service.dto.DevicePhotoQueryCriteria;
import me.zhengjie.modules.smart.service.mapstruct.DevicePhotoMapper;
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
* @date 2021-02-20
**/
@Service
@RequiredArgsConstructor
public class DevicePhotoServiceImpl implements DevicePhotoService {

    private final DevicePhotoRepository devicePhotoRepository;
    private final DevicePhotoMapper devicePhotoMapper;

    @Override
    public Map<String,Object> queryAll(DevicePhotoQueryCriteria criteria, Pageable pageable){
        Page<DevicePhoto> page = devicePhotoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(devicePhotoMapper::toDto));
    }

    @Override
    public List<DevicePhotoDto> queryAll(DevicePhotoQueryCriteria criteria){
        return devicePhotoMapper.toDto(devicePhotoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DevicePhotoDto findById(Long id) {
        DevicePhoto devicePhoto = devicePhotoRepository.findById(id).orElseGet(DevicePhoto::new);
        ValidationUtil.isNull(devicePhoto.getId(),"DevicePhoto","id",id);
        return devicePhotoMapper.toDto(devicePhoto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DevicePhotoDto create(DevicePhoto resources) {
        return devicePhotoMapper.toDto(devicePhotoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DevicePhoto resources) {
        DevicePhoto devicePhoto = devicePhotoRepository.findById(resources.getId()).orElseGet(DevicePhoto::new);
        ValidationUtil.isNull( devicePhoto.getId(),"DevicePhoto","id",resources.getId());
        devicePhoto.copy(resources);
        devicePhotoRepository.save(devicePhoto);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            devicePhotoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DevicePhotoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DevicePhotoDto devicePhoto : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("传感器id", devicePhoto.getDeviceid());
            map.put("文件真实的名称", devicePhoto.getRealName());
            map.put("文件名", devicePhoto.getName());
            map.put("后缀", devicePhoto.getSuffix());
            map.put("路径", devicePhoto.getPath());
            map.put("类型", devicePhoto.getType());
            map.put("大小", devicePhoto.getSize());
            map.put("创建者", devicePhoto.getCreateBy());
            map.put("更新者", devicePhoto.getUpdateBy());
            map.put("创建日期", devicePhoto.getCreateTime());
            map.put("更新时间", devicePhoto.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}