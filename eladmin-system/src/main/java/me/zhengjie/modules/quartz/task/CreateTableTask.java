package me.zhengjie.modules.quartz.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateTableTask {
    public void run(){
        // 每月1日 0时0分0秒  建子表，修改主表union
        // 修改主表 alter table tb_member UNION=(tb_member1,tb_member2,tb_member3);
        log.info("run 执行成功");
    }
}
