package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @author lhl
* @date 2019-10-22
*/
public interface SProjectRepository extends JpaRepository<SProject, Long>, JpaSpecificationExecutor {
    @Query(value = "select p.* from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1 ", nativeQuery = true)
    List<SProject> findProjectByUserid(String userid);
    @Query(value = "select p.* from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid = ?1",
            countQuery = "select count(1) from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid = ?1 ",
            nativeQuery = true)
    Page<SProject> getSProjectsByUserid(String userid, Pageable pageable);
    @Query(value = "select p.* from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid = ?1 and p.proname like ?2 ",
            countQuery = "select count(1) from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid = ?1 and p.proname like ?2 ",
            nativeQuery = true)
    Page<SProject> getSProjectsByUseridAndProname(String userid, String proname, Pageable pageable);
}