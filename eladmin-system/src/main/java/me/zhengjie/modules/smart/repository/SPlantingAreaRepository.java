package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SPlantingArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
* @author liujing
* @date 2019-11-22
*/
public interface SPlantingAreaRepository extends JpaRepository<SPlantingArea, Long>, JpaSpecificationExecutor {
    List<SPlantingArea> findByProjectid(String projectid);
}