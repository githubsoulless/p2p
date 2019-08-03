package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

import java.util.List;
import java.util.Map;

public interface RechargeRecordService {
    List<RechargeRecord> queryRecentRechargeRecordListByUid(Map<String, Object> paramMap);

    int addRecharge(RechargeRecord rechargeRecord);

    int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    RechargeRecord queryRechargeRecordByRechardNo(String out_trade_no);

    int recharge(Map<String, Object> paramMap);

    void dealRechargeRecord();
}
