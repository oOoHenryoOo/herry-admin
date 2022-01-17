package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SDeviceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author lhl
* @date 2019-10-30
*/
public interface SDeviceLogRepository extends JpaRepository<SDeviceLog, Long>, JpaSpecificationExecutor {
    Page<SDeviceLog> findByDeviceid(String deviceid, Pageable pageable);
}