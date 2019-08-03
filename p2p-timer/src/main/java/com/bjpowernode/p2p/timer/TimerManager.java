package com.bjpowernode.p2p.timer;

import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimerManager {
    //创建日志记录对象
    private Logger logger = LogManager.getLogger(TimerManager.class);

    @Autowired
    private IncomeRecordService incomeRecordService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

//    每隔5秒执行一次,收益计划
    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomePlan(){
        logger.info("-------------生成收益计划开始-------------");

        incomeRecordService.generateIncomePlan();

        logger.info("-------------生成收益计划结束-------------");
    }

//    收益返还
@Scheduled(cron = "0/5 * * * * ?")
public void generateIncomeBack() {
    logger.info("--------收益返还开始-----------");

    incomeRecordService.generateIncomeBack();

    logger.info("--------收益返还结束-----------");
}

//  处理掉单
    public void dealRechargeRecord(){
       logger.info("-----------处理掉单开始-------------");

       rechargeRecordService.dealRechargeRecord();
       logger.info("-----------处理掉单结束-------------");
    }
}
