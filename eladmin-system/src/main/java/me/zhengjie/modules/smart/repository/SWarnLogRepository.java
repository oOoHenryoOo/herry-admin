package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SWarnLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

/**
* @author lhl
* @date 2020-01-03
*/
public interface SWarnLogRepository extends JpaRepository<SWarnLog, Long>, JpaSpecificationExecutor {
    @Query(value = "select * from s_warn_log w where w.deviceid in (" +
            " select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
            "                         in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1 ))  and if (?2 = '%%', 1=1, w.deviceid like ?2)  ",
            countQuery = "select count(1) from s_warn_log w where w.deviceid in (" +
                    "  select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
                    "                  in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1 ))  and if (?2 = '%%', 1=1, w.deviceid like ?2)  ",
            nativeQuery = true)
    Page<SWarnLog> getSWarnsByUseridNoType(String userid, String deviceid, Pageable pageable);

    List<SWarnLog> findAllByCommondEqualsAndMsgEqualsAndCreateTimeGreaterThan(String command, String msg, Timestamp createTime);
}