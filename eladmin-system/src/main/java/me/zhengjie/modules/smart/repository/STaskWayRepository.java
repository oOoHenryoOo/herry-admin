package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.STaskWay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
* @author lhl
* @date 2019-11-18
*/
public interface STaskWayRepository extends JpaRepository<STaskWay, Long>, JpaSpecificationExecutor {
    void deleteByTaskid(String taskid);

    List<STaskWay> findByTaskid(String taskid);
}