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

import me.zhengjie.modules.smart.domain.PhotoData;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.smart.repository.PhotoDataRepository;
import me.zhengjie.modules.smart.service.PhotoDataService;
import me.zhengjie.modules.smart.service.dto.PhotoDataDto;
import me.zhengjie.modules.smart.service.dto.PhotoDataQueryCriteria;
import me.zhengjie.modules.smart.service.mapstruct.PhotoDataMapper;
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
public class PhotoDataServiceImpl implements PhotoDataService {

    private final PhotoDataRepository photoDataRepository;
    private final PhotoDataMapper photoDataMapper;

    @Override
    public Map<String,Object> queryAll(PhotoDataQueryCriteria criteria, Pageable pageable){
        Page<PhotoData> page = photoDataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(photoDataMapper::toDto));
    }

    @Override
    public List<PhotoDataDto> queryAll(PhotoDataQueryCriteria criteria){
        return photoDataMapper.toDto(photoDataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PhotoDataDto findById(Long id) {
        PhotoData photoData = photoDataRepository.findById(id).orElseGet(PhotoData::new);
        ValidationUtil.isNull(photoData.getId(),"PhotoData","id",id);
        return photoDataMapper.toDto(photoData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoDataDto create(PhotoData resources) {
        return photoDataMapper.toDto(photoDataRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PhotoData resources) {
        PhotoData photoData = photoDataRepository.findById(resources.getId()).orElseGet(PhotoData::new);
        ValidationUtil.isNull( photoData.getId(),"PhotoData","id",resources.getId());
        photoData.copy(resources);
        photoDataRepository.save(photoData);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            photoDataRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PhotoDataDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PhotoDataDto photoData : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("设备ID", photoData.getDeviceid());
            map.put("图片名称", photoData.getPhotorealname());
            map.put("害虫代码", photoData.getPestid());
            map.put("害虫名称", photoData.getPestname());
            map.put("害虫数量", photoData.getPestcount());
            map.put("图片时间", photoData.getPictime());
            map.put("识别时间", photoData.getDatatime());
            map.put("备注", photoData.getNt());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}