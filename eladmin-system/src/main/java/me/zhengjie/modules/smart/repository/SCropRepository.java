package me.zhengjie.modules.smart.repository;

import org.springframework.data.jpa.repository.Query;
import me.zhengjie.modules.smart.domain.SCrop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
* @author lhl
* @date 2019-11-14
*/
public interface SCropRepository extends JpaRepository<SCrop, Long>, JpaSpecificationExecutor {
    @Query(value="select * from s_crop where projectid = ?1", nativeQuery = true)
    List<SCrop> queryCropByProjectId(String projectId);

    List<SCrop> findByProjectid(String projectid);
}