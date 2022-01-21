package me.zhengjie.modules.smart.repository;

import me.zhengjie.modules.smart.domain.SItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @author lhl
* @date 2019-10-21
*/
public interface SItemRepository extends JpaRepository<SItem, Long>, JpaSpecificationExecutor {
}