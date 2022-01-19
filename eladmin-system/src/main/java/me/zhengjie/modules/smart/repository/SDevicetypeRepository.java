package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SDevicetype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author lhl
* @date 2019-10-21
*/
public interface SDevicetypeRepository extends JpaRepository<SDevicetype, Long>, JpaSpecificationExecutor {
}