package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public List<IncomeRecord> queryRecentIncomeRecordListByUid(Map<String, Object> paramMap) {
        return incomeRecordMapper.selectRecentIncomeRecordListByUid(paramMap);
    }

    /*
    * 生成收益计划
    * */
    @Override
    public void generateIncomePlan() {

//        获取已满标的产品，放到list集合中
        List<LoanInfo> loanInfoList =  loanInfoMapper.selectLoanInfoByProductStatus(1);



//        循环遍历，获取到某一个已满标的产品
        for(LoanInfo loanInfo:loanInfoList){

//        获取当前产品的投资记录,返回一个列表
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoByLoanId(loanInfo.getId());


//        一条投资记录对应一条收益计划，循环遍历投资记录
            for(BidInfo bidInfo:bidInfoList){
//                生成收益计划
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setLoanId(bidInfo.getLoanId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);//0未返还1已返还

                //收益时间(Date) = 产品满标时间(Date) + 产品周期(int)
                Date incomeDate = null;

                //收益金额 = 投资金额 * 日利率 * 投资天数;
                Double incomeMoney = null;

//                新手宝
                if(Constants.PRODUCT_TYPE_X == loanInfo.getProductType()){
                    incomeDate = DateUtils.getDateByAddDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney=bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();
                }else {
//                 优选或散标
                    incomeDate = DateUtils.getDateByAddMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney=bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();
                }

                incomeMoney = Math.round(incomeMoney * Math.pow(10,2))/Math.pow(10,2);

                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                incomeRecordMapper.insertSelective(incomeRecord);
            }


            //更新产品的状态为满标且生成收益计划
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanInfo.getId());
            updateLoanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
        }
    }

//    收益返还
    @Override
    public void generateIncomeBack() {
//     怎么确定收益时间与当前时间一致
//        收益状态为0
        List<IncomeRecord> incomeRecordList =incomeRecordMapper.selectIncomeRecordByIncomeStatusAndCurrentDate(0);
        for(IncomeRecord incomeRecord:incomeRecordList){
//            每条收益记录找出对应的用户，收益叠加返还给用户账户
            //将当前收益计划的收益金额及本金返还给对应的用户帐户
            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());

            int count = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

            //更新当前收益计划的状态为1已返还
            IncomeRecord updateIncomeRecord = new IncomeRecord();
            updateIncomeRecord.setId(incomeRecord.getId());
            updateIncomeRecord.setIncomeStatus(1);
            incomeRecordMapper.updateByPrimaryKeySelective(updateIncomeRecord);
        }
    }

}
