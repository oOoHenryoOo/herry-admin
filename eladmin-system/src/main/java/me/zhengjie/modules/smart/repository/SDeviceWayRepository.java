package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SDeviceWay;
import me.zhengjie.modules.smart.service.dto.SDeviceWayDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author lhl
* @date 2019-10-23
*/
public interface SDeviceWayRepository extends JpaRepository<SDeviceWay, Long>, JpaSpecificationExecutor {
    @Transactional
    void deleteByDeviceid(String deviceid);

    List<SDeviceWay> findByDeviceid(String deviceid);

    SDeviceWay findByDeviceidAndWay(String deviceid, String way);
}