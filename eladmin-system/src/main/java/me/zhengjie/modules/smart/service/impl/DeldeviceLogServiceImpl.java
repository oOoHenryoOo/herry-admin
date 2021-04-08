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

import me.zhengjie.modules.smart.domain.DeldeviceLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.smart.repository.DeldeviceLogRepository;
import me.zhengjie.modules.smart.service.DeldeviceLogService;
import me.zhengjie.modules.smart.service.dto.DeldeviceLogDto;
import me.zhengjie.modules.smart.service.dto.DeldeviceLogQueryCriteria;
import me.zhengjie.modules.smart.service.mapstruct.DeldeviceLogMapper;
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
public class DeldeviceLogServiceImpl implements DeldeviceLogService {

    private final DeldeviceLogRepository deldeviceLogRepository;
    private final DeldeviceLogMapper deldeviceLogMapper;

    @Override
    public Map<String,Object> queryAll(DeldeviceLogQueryCriteria criteria, Pageable pageable){
        Page<DeldeviceLog> page = deldeviceLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(deldeviceLogMapper::toDto));
    }

    @Override
    public List<DeldeviceLogDto> queryAll(DeldeviceLogQueryCriteria criteria){
        return deldeviceLogMapper.toDto(deldeviceLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DeldeviceLogDto findById(Long id) {
        DeldeviceLog deldeviceLog = deldeviceLogRepository.findById(id).orElseGet(DeldeviceLog::new);
        ValidationUtil.isNull(deldeviceLog.getId(),"DeldeviceLog","id",id);
        return deldeviceLogMapper.toDto(deldeviceLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeldeviceLogDto create(DeldeviceLog resources) {
        return deldeviceLogMapper.toDto(deldeviceLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeldeviceLog resources) {
        DeldeviceLog deldeviceLog = deldeviceLogRepository.findById(resources.getId()).orElseGet(DeldeviceLog::new);
        ValidationUtil.isNull( deldeviceLog.getId(),"DeldeviceLog","id",resources.getId());
        deldeviceLog.copy(resources);
        deldeviceLogRepository.save(deldeviceLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            deldeviceLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DeldeviceLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeldeviceLogDto deldeviceLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("设备ID", deldeviceLog.getDeviceid());
            map.put("用户", deldeviceLog.getUsername());
            map.put("日志时间", deldeviceLog.getLogtime());
            map.put("设备类型", deldeviceLog.getDevicetype());
            map.put("备注", deldeviceLog.getNt());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}