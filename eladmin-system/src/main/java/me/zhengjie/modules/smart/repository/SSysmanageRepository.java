package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SSysmanage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author lhl
* @date 2019-11-11
*/
public interface SSysmanageRepository extends JpaRepository<SSysmanage, Long>, JpaSpecificationExecutor {
    SSysmanage findByUserid(String userid);
}