package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author lhl
* @date 2019-11-14
*/
public interface SPersonnelRepository extends JpaRepository<SPersonnel, Long>, JpaSpecificationExecutor {
}