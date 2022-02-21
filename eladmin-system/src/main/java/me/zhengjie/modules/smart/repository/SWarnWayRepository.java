package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SWarnWay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
* @author lhl
* @date 2020-01-03
*/
public interface SWarnWayRepository extends JpaRepository<SWarnWay, Long>, JpaSpecificationExecutor {
    List<SWarnWay> findByWarnidAndFlag(String warnid, String flag);

    List<SWarnWay> findAllByWarnidAndFlagAndValid(String warnid, String flag, String valid);

    void deleteByWarnid(String warnid);

    List<SWarnWay> findByWarnid(String warnid);
}