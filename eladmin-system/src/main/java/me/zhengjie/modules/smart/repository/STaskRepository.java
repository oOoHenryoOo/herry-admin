package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.STask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
* @author lhl
* @date 2019-11-14
*/
public interface STaskRepository extends JpaRepository<STask, Long>, JpaSpecificationExecutor {

    List<STask> findAllByDeviceidIn(List<String> deviceidList);
}