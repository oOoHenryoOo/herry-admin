package me.zhengjie.modules.smart.repository;

import org.springframework.data.jpa.repository.Query;
import me.zhengjie.modules.smart.domain.SDevice;
import me.zhengjie.modules.smart.domain.SProjectDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author lhl
* @date 2019-10-23
*/
public interface SProjectDeviceRepository extends JpaRepository<SProjectDevice, Long>, JpaSpecificationExecutor {
    SProjectDevice findByProjectidAndDeviceid(String projectid, String deviceid);

    SProjectDevice findByDeviceid(String deviceId);
    @Transactional
    void deleteByProjectidAndDeviceid(String projectid, String deviceid);

    void deleteByDeviceid(String deviceid);
}