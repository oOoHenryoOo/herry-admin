package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SDevice;
import me.zhengjie.modules.smart.service.dto.SDeviceQueryCriteria;
import me.zhengjie.modules.system.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @author lhl
* @date 2019-10-23
*/
public interface SDeviceRepository extends JpaRepository<SDevice, String>, JpaSpecificationExecutor {
    SDevice findByDeviceid(String deviceid);

    SDevice findByImei(String imei);

    @Query(value = "select p.projectid nt,d.* from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1 ) and p.valid = '1' ",
            countQuery = "select count(1) from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where  p.projectid in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid  where u.userid=?1) and p.valid = '1'  ",
            nativeQuery = true)
    Page<SDevice> queryAllByUserid(String userid, Pageable pageable);

    @Query(value = "select p.projectid nt,d.* from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid " +
            "    in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.deviceid like ?2  and p.valid = '1' ",
            countQuery = "select count(1) from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
            "    in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.deviceid like ?2  and p.valid = '1' ",
            nativeQuery = true)
    Page<SDevice> queryAllByUseridAndDeviceid(String userid,String deviceid, Pageable pageable);

    @Query(value = "select p.projectid nt,d.* from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid " +
            "    in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.imei like ?2  and p.valid = '1' ",
            countQuery = "select count(1) from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid "+
            "    in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.imei like ?2  and p.valid = '1' ",
            nativeQuery = true)
    Page<SDevice> queryAllByUseridAndImei(String userid, String imei, Pageable pageable);

    @Query(value = "select p.projectid nt,d.* from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid " +
            "    in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.name like ?2  and p.valid = '1' ",
            countQuery = "select count(1) from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
            "    in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.name like ?2  and p.valid = '1' ",
            nativeQuery = true)
    Page<SDevice> queryAllByUseridAndName(String userid, String name, Pageable pageable);

    @Query(value = "select CASE  " +
            "WHEN (unix_timestamp()-unix_timestamp(d.onlinetime)) <= ((d.timetrack2)/1000)*?2 THEN '在线' " +
            "ELSE '离线' END online " +
            ",p.projectid nt,d.* from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in  " +
            " (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.relay in ('1','2') and p.valid = '1' ",
            countQuery = "select count(1) from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in   " +
            " (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.relay in ('1','2')  and p.valid = '1' ",
            nativeQuery = true)
    Page<SDevice> queryAllByUseridAnddevType(String userid, int offline, String relay, Pageable pageable);

    @Query(value = "select CASE  " +
            "WHEN (unix_timestamp()-unix_timestamp(d.onlinetime)) <= ((d.timetrack2)/1000)*?3 THEN '在线' " +
            "ELSE '离线' END online " +
            ",p.projectid nt,d.* from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in  " +
            " (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.relay = ?2  and p.valid = '1' ",
            countQuery = "select count(1) from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in  " +
            " (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and d.relay = ?2  and p.valid = '1' ",
            nativeQuery = true)
    Page<SDevice> queryAllByUseridNotdevType(String userid, String relay,int offline, Pageable pageable);

    @Query(value = "select CASE  " +
            "WHEN (unix_timestamp()-unix_timestamp(d.onlinetime)) <= ((d.timetrack2)/1000)*?2 THEN '在线' " +
            "ELSE '离线' END online " +
            ",d.* from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in  " +
            " (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where d.relay = '0' and u.userid=?1)  and p.valid = '1' ",
            nativeQuery = true)
    List<SDevice> queryAllByUseridNoPage(String userid, int offline);

    @Query(value = "select d.* from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in  " +
            " (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where d.relay = '1' and u.userid=?1)  and p.valid = '1' ",
            nativeQuery = true)
    List<SDevice> queryAllRelayByUseridNoPage(String userid);

    @Query(value = "select d.* from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in   " +
            " (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where d.relay = '0' and u.userid=?1) and p.valid = '1'  limit 0,1 ",
            nativeQuery = true)
    SDevice loadOneDevice(String userid);

    @Query(value = "select count(*) count from ( " +
            "select  " +
            "CASE  " +
            "WHEN (unix_timestamp()-unix_timestamp(d.onlinetime)) <= ((d.timetrack2)/1000)*?3 THEN '801on' " +
            "ELSE '801off' END online " +
            ",d.id from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in   " +
            "     (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid= ?1) and d.relay = '1' " +
            ") a where a.online=?2 GROUP BY a.online ",
            nativeQuery = true)
    Integer getOnCount(String userid, String onStr,int offline);

    @Query(value = "select count(*) timetrack,b.online from ( " +
            "select  " +
            "CASE  " +
            "WHEN (unix_timestamp()-unix_timestamp(d.onlinetime)) <= ((d.timetrack2)/1000)*?3 THEN 'devon' " +
            "ELSE 'devoff' END online " +
            ",d.id from s_device d LEFT JOIN s_project_device p on d.deviceid =  p.deviceid where p.projectid in   " +
            "     (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid= ?1) and d.relay = '0' " +
            "  )b where b.online=?2  GROUP BY b.online  ",
            nativeQuery = true)
    Integer getDevOnCount(String userid, String devonStr,int offline);

    @Query(value = "select d.* from s_project_device sp LEFT JOIN s_device d ON d.deviceid = sp.deviceid where " +
            "sp.projectid = ?1 and d.relay = ?2",
            nativeQuery = true)
    List<SDevice> getDeviceByProjectAndType(String projectId, String type);

    @Query(value = "select d.* from s_device d LEFT JOIN s_project_device p ON d.deviceid = p.deviceid where d.relay = '0' and p.projectid = ?1 ORDER BY onlinetime DESC ",
            nativeQuery = true)
    List<SDevice> getDeviceByProjectid(Long id);

    @Query(value = "select d.* from s_device d LEFT JOIN s_project_device p ON d.deviceid = p.deviceid where p.projectid = ?1 ORDER BY onlinetime DESC ",
            nativeQuery = true)
    List<SDevice> getDeviceAndRelayByProjectid(Long projectid);
}