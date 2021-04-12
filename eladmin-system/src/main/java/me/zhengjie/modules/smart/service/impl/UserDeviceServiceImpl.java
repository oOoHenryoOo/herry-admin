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

import me.zhengjie.modules.smart.domain.UserDevice;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.smart.repository.UserDeviceRepository;
import me.zhengjie.modules.smart.service.UserDeviceService;
import me.zhengjie.modules.smart.service.dto.UserDeviceDto;
import me.zhengjie.modules.smart.service.dto.UserDeviceQueryCriteria;
import me.zhengjie.modules.smart.service.mapstruct.UserDeviceMapper;
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
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final UserDeviceMapper userDeviceMapper;

    @Override
    public Map<String,Object> queryAll(UserDeviceQueryCriteria criteria, Pageable pageable){
        Page<UserDevice> page = userDeviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(userDeviceMapper::toDto));
    }

    @Override
    public List<UserDeviceDto> queryAll(UserDeviceQueryCriteria criteria){
        return userDeviceMapper.toDto(userDeviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public UserDeviceDto findById(Long id) {
        UserDevice userDevice = userDeviceRepository.findById(id).orElseGet(UserDevice::new);
        ValidationUtil.isNull(userDevice.getId(),"UserDevice","id",id);
        return userDeviceMapper.toDto(userDevice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDeviceDto create(UserDevice resources) {
        return userDeviceMapper.toDto(userDeviceRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserDevice resources) {
        UserDevice userDevice = userDeviceRepository.findById(resources.getId()).orElseGet(UserDevice::new);
        ValidationUtil.isNull( userDevice.getId(),"UserDevice","id",resources.getId());
        userDevice.copy(resources);
        userDeviceRepository.save(userDevice);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            userDeviceRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<UserDeviceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserDeviceDto userDevice : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户ID", userDevice.getUserid());
            map.put("设备ID", userDevice.getDeviceid());
            map.put("0:设备原主人1:普通关联者", userDevice.getIsadmin());
            map.put("创建时间", userDevice.getCreateTime());
            map.put("更新时间", userDevice.getUpdateTime());
            map.put("备注", userDevice.getNt());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}