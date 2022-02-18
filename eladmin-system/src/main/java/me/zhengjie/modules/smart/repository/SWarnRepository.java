package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SWarn;
import me.zhengjie.modules.smart.service.dto.SWarnDTO;
import me.zhengjie.modules.smart.service.dto.SWarnQueryCriteria;
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
public interface SWarnRepository extends JpaRepository<SWarn, Long>, JpaSpecificationExecutor {
    @Query(value="select * from s_warn where deviceid = ?1 and item = ?2 and enable = 1", nativeQuery = true)
    SWarn queryWarnByDeviceIdAndItem (String deviceId, String item);

    List<SWarn> findByDeviceidAndItem(String deviceid, String item);

    @Query(value = "select d.* from s_warn d where d.deviceid in ( " +
            " select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
            "             in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and  p.valid = '1') and if (?2 = '%%', 1=1, d.deviceid like ?2) and if (?3 = '%%', 1=1, d.item like ?3) and d.warntype in ?4",
            countQuery = "select count(1) from s_warn d where d.deviceid in ( " +
                    " select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
                    "             in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and  p.valid = '1') and if (?2 = '%%', 1=1, d.deviceid like ?2) and if (?3 = '%%', 1=1, d.item like ?3) and d.warntype in ?4",
            nativeQuery = true)
    Page<SWarn> getSWarnsByUserid(String userid, String deviceid, String item, List<String> types, Pageable pageable);

    @Query(value = "select d.* from s_warn d where d.deviceid in ( " +
            " select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
            "             in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and  p.valid = '1') and if (?2 = '%%', 1=1, d.deviceid like ?2) and if (?3 = '%%', 1=1, d.item like ?3) ",
            countQuery = "select count(1) from s_warn d where d.deviceid in ( " +
                    " select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
                    "             in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and  p.valid = '1') and if (?2 = '%%', 1=1, d.deviceid like ?2) and if (?3 = '%%', 1=1, d.item like ?3) ",
            nativeQuery = true)
    Page<SWarn> getSWarnsByUseridNoType(String userid, String deviceid, String item, Pageable pageable);

    SWarn findByItemEqualsAndNameLike(String item, String name);
}