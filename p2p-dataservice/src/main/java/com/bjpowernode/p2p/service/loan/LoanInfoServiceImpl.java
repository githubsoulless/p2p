package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Override
    public Double queryhistoryAverageRate() {
        //如果缓存中有数据，则从缓存中取值，如果没有则从数据库中取值，再放到缓存中
        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);


        if(null == historyAverageRate) {
            //设置同步代码块，为了避免redis穿透，也就是多次从数据库中取值
            synchronized (this) {

                //进行双重检测
                //继续从redis中取值，如果没有再从数据库中查找
                historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);


                if (null == historyAverageRate) {
                    //从数据库中查询
                    historyAverageRate = loanInfoMapper.selectHistoryAverageRate();
                    //把值放入到redis中
                    redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE, historyAverageRate, 15, TimeUnit.MINUTES);
                    System.out.println("从数据库中查找。。");

                } else {
                    System.out.println("从redis中取值");
                }
            }
        }else{
            System.out.println("从redis中取值");
        }

        return historyAverageRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanInfoListByProductType(paramMap);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap) {

        PaginationVO paginationVO = new PaginationVO();
        List<LoanInfo> dataList = loanInfoMapper.selectLoanInfoListByProductType(paramMap);
        Long total = loanInfoMapper.selectTotalLoanInfo(paramMap);
        paginationVO.setTotal(total);
        paginationVO.setDataList(dataList);
        return paginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoListById(Integer id) {
//        根据产品id获取产品详情，返回对象
        return loanInfoMapper.selectByPrimaryKey(id);

    }

}
