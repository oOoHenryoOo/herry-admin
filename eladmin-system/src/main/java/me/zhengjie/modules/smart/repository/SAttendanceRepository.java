package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @author lhl
* @date 2019-11-14
*/
public interface SAttendanceRepository extends JpaRepository<SAttendance, Long>, JpaSpecificationExecutor {
    @Query(value="select * from S_attendance where attTime >= ?1 and attTime <= ?2 and projectid = ?3 ORDER BY attTime ", nativeQuery = true)
    List<SAttendance> queryAttendanceByDate(String startTime, String endTime, String projectid);
}