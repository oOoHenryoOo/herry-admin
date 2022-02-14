package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SWarnmsg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author lhl
* @date 2019-10-22
*/
public interface SWarnmsgRepository extends JpaRepository<SWarnmsg, Long>, JpaSpecificationExecutor {
    Map map = new HashMap();

    @Query(value="select * from s_warnmsg where deviceid = ?1 and item = ?2 and flg= '0' group by dtime DESC limit 1", nativeQuery = true)
    SWarnmsg queryByDeviceIdAndItem (String deviceId, String item);

    @Query(value = "select d.* from s_warnmsg d where d.deviceid in ( " +
            " select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
            "             in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and  p.valid = '1') and if (?2 = '%%', 1=1, d.deviceid like ?2) and if (?3 = '%%', 1=1, d.item like ?3)",
            countQuery = "select count(1) from s_warnmsg d where d.deviceid in ( " +
                    " select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
                    "             in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and  p.valid = '1') and if (?2 = '%%', 1=1, d.deviceid like ?2) and if (?3 = '%%', 1=1, d.item like ?3)",
            nativeQuery = true)
    Page<SWarnmsg> getSWarnmsgsByUserid(String userid, String deviceid, String item, Pageable pageable);

    @Query(value =  "select count(1) from s_warnmsg d where d.deviceid in ( " +
                    " select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid  " +
                    "             in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=?1) and  p.valid = '1') and d.flg = '0'",
            nativeQuery = true)
    int getWarnmsgCount(String userid);

    @Query(value =  "select dt.rq,ifnull(da.count,0) count from ( " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 11 MONTH) ,'%Y/%m' ) rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 10 MONTH) ,'%Y/%m' ) rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 9 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 8 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 7 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 6 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 5 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 4 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 3 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 2 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(DATE_SUB(NOW(), INTERVAL 1 MONTH) ,'%Y/%m' )  rq " +
            "   UNION " +
            "   select date_format(NOW() ,'%Y/%m' )  " +
            ") dt LEFT JOIN ( " +
            "   select date_format(d.dtime ,'%Y/%m' ) rq,COUNT(*) count from s_warnmsg d where d.deviceid in (  " +
            "       select d.deviceid from s_device d LEFT JOIN s_project_device p on d.deviceid = p.deviceid where p.projectid   " +
            "       in (select p.id from s_project p LEFT JOIN s_project_user u ON p.id = u.projectid where u.userid=12) and  p.valid = '1')  " +
            " GROUP BY date_format(d.dtime ,'%Y/%m' ) " +
            ") da on dt.rq = da.rq",
            nativeQuery = true)
    List<Map<String, String>> getWarnmsgCountByUserid(String userid);
}