package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.RecentBidInfoVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

//    定义redis缓存对象
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Override
    public Double queryAllBidMoney() {

        //Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);
        //首先获取操作某种数据类型的对象
        BoundValueOperations<Object,Object> boundValueOperations = redisTemplate.boundValueOps(Constants.ALL_BID_MONEY);

        //再从该对象中获取该值
        Double allBidMoney = (Double) boundValueOperations.get();


        //判断缓存中是否有值
        if(null == allBidMoney){

            //同步代码块
            synchronized (this){
                 allBidMoney = (Double) boundValueOperations.get();

                 if(null == allBidMoney){
                     allBidMoney = bidInfoMapper.selectAllBidMoney();
                     //把值放到缓存中
                     boundValueOperations.set(allBidMoney, 15, TimeUnit.MINUTES);
                     System.out.println("从数据库中取值");
                 }else{
                     System.out.println("从缓存中取值");
                 }
            }
            System.out.println("从数据库中取值");
        }else{
            System.out.println("从缓存中取值");
        }
        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryRecentBidInfoListById(Map<String, Object> paramMap) {

//
        return  bidInfoMapper.selectRecentBidInfoListById(paramMap);

    }

    @Override
    public List<RecentBidInfoVO> queryRecentBidInfoListByUid(Map<String, Object> paramMap) {
        return bidInfoMapper.selectRecentlyBidInfoListByUid(paramMap);

    }

    @Override
    public ResultObject invest(Map<String, Object> paramMap) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);

        Integer loanId = (Integer) paramMap.get("loanId");
        Integer uid = (Integer) paramMap.get("uid");
        Double bidMoney = (Double) paramMap.get("bidMoney");

        //更新产品的剩余可投金额
        //超卖：实际销售的数量超过了库存数量
        //使用数据库乐观锁机制来解决超卖
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        paramMap.put("version",loanInfo.getVersion());

        int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoneyByLoanId(paramMap);

        if (updateLeftProductMoneyCount > 0) {

            //更新用户帐户余额
            int updateFinanceCount = financeAccountMapper.updateFinanceAccountByBid(paramMap);

            if (updateFinanceCount > 0) {
                //新增投资记录
                BidInfo bidInfo = new BidInfo();
                bidInfo.setUid(uid);
                bidInfo.setLoanId(loanId);
                bidInfo.setBidMoney(bidMoney);
                bidInfo.setBidTime(new Date());
                bidInfo.setBidStatus(1);
                int insertBidInfoCount = bidInfoMapper.insertSelective(bidInfo);
                String phone = (String) paramMap.get("phone");

                if (insertBidInfoCount > 0) {
                    //再次查询产品的详情
                    LoanInfo loanDetail = loanInfoMapper.selectByPrimaryKey(loanId);

                    //判断产品是否满标
                    if (0 == loanDetail.getLeftProductMoney()) {

                        //已满标 -> 更新产品的状态及满标时间
                        LoanInfo updateLoanInfo = new LoanInfo();
                        updateLoanInfo.setId(loanDetail.getId());
                        updateLoanInfo.setProductFullTime(new Date());
                        updateLoanInfo.setProductStatus(1);
                        int i = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
                        if (i <= 0) {
                            resultObject.setErrorCode(Constants.FAIL);
                        }
                    }
//                  将用户投资信息放到redis缓存中
                    redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP, phone, bidMoney);
                    System.out.println(resultObject);

                } else {
                    resultObject.setErrorCode(Constants.FAIL);
                }
            } else {
                resultObject.setErrorCode(Constants.FAIL);
            }

        } else {
            resultObject.setErrorCode(Constants.FAIL);
        }

        return resultObject;
    }

    @Override
    public PaginationVO<RecentBidInfoVO> queryBidInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<RecentBidInfoVO> paginationVO = new PaginationVO<>();

        Long total = bidInfoMapper.selectTotal(paramMap);

        paginationVO.setTotal(total);

        List<RecentBidInfoVO> recentBidInfoVOList = bidInfoMapper.selectRecentlyBidInfoListByUid(paramMap);

        paginationVO.setDataList(recentBidInfoVOList);

        return paginationVO;
    }

    @Override
    public List<BidUser> queryBidUserTop() {
//        获取投资排行榜，使用redis的zset
        List<BidUser> bidInfoList = new ArrayList<BidUser>();
//        从redis中取出投资记录，前提是redis中存放了记录，倒序取出，前5条
//        所以要在投资时，（上上步）把数据放入缓存中
//        ZSetOperations.TypedTuple<Object>应该是键的类型
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(Constants.INVEST_TOP, 0,5);

//        通过迭代器获得对象中的数据
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();

        while(iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            BidUser bidUser = new BidUser();
            bidUser.setPhone((String) next.getValue());
            bidUser.setScore(next.getScore());
            bidInfoList.add(bidUser);
        }

        return bidInfoList;
    }


}
