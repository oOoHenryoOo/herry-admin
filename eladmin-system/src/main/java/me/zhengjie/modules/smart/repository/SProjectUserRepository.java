package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
* @author lhl
* @date 2019-10-23
*/
public interface SProjectUserRepository extends JpaRepository<SProjectUser, Long>, JpaSpecificationExecutor {
    SProjectUser findByProjectidAndUserid(String projectid, String userid);
}