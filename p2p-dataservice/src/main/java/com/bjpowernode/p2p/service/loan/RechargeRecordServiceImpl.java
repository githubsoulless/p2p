package com.bjpowernode.p2p.service.loan;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public List<RechargeRecord> queryRecentRechargeRecordListByUid(Map<String, Object> paramMap) {
        return rechargeRecordMapper.selectRecentRechargeRecordListByUid(paramMap);
    }

    @Override
    public int addRecharge(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }

    @Override
    public RechargeRecord queryRechargeRecordByRechardNo(String out_trade_no) {
        return rechargeRecordMapper.selectRechargeRecordByRechardNo(out_trade_no);
    }

    @Override
    public int recharge(Map<String, Object> paramMap) {
//        更新当前账户余额
        int financeAccountCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
//        如果更新成功，则更新充值记录的状态
        if(financeAccountCount > 0){
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setRechargeStatus("1");
            rechargeRecord.setRechargeNo((String) paramMap.get("rechargeNo"));
            int updateCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
            if (updateCount <= 0){
                return 0;
            }

        }else{
            return 0;
        }

        return 1;
    }

//    处理掉单业务
    @Override
    public void dealRechargeRecord() {

//获取充值记录状态为0 -> 返回List<充值记录>
        List<RechargeRecord> rechargeRecordList = rechargeRecordMapper.selectRechargeRecordByStatus(0);

        //循环遍历，获取每一条充值记录
        for (RechargeRecord rechargeRecord : rechargeRecordList) {
            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("out_trade_no",rechargeRecord.getRechargeNo());

            try {
                //根据充值订单号查询该笔订单的状态
                String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);

                //将json格式的字符串转换为JSON对象
                JSONObject jsonObject = JSONObject.parseObject(jsonString);

                JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

                //获取通信标识code
                String code = tradeQueryResponse.getString("code");

                if ("10000".equals(code)) {

                    //获取trade_status
                    String tradeStatus = tradeQueryResponse.getString("trade_status");

                    if ("TRADE_CLOSED".equals(tradeStatus)) {
                        RechargeRecord updateRecharge = new RechargeRecord();
                        updateRecharge.setRechargeNo(rechargeRecord.getRechargeNo());
                        updateRecharge.setRechargeStatus("2");
                        rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRecharge);
                    }

                    if ("TRADE_SUCCESS".equals(tradeStatus)) {

                        RechargeRecord rechargeDetail = rechargeRecordMapper.selectRechargeRecordByRechardNo(rechargeRecord.getRechargeNo());

                        if ("0".equals(rechargeDetail.getRechargeStatus())) {
                            paramMap.put("uid",rechargeRecord.getUid());
                            paramMap.put("rechargeMoney",rechargeRecord.getRechargeMoney());
                            int i = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
                            if (i > 0) {
                                RechargeRecord updateRechargeRecord = new RechargeRecord();
                                updateRechargeRecord.setRechargeNo(rechargeRecord.getRechargeNo());
                                updateRechargeRecord.setRechargeStatus("1");
                                rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRechargeRecord);
                            }

                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
