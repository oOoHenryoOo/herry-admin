package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author lhl
* @date 2019-10-22
*/
public interface SDataRepository extends JpaRepository<SData, Long>, JpaSpecificationExecutor {
}