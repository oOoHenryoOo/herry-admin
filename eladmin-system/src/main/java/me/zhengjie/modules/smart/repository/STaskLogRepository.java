package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.STaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author lhl
* @date 2019-11-14
*/
public interface STaskLogRepository extends JpaRepository<STaskLog, Long>, JpaSpecificationExecutor {
}