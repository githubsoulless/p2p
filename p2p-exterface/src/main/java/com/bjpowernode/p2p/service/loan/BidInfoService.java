package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.RecentBidInfoVO;
import com.bjpowernode.p2p.model.vo.ResultObject;

import java.util.List;
import java.util.Map;

public interface BidInfoService {

    Double queryAllBidMoney();

    List<BidInfo> queryRecentBidInfoListById(Map<String, Object> paramMap);

    List<RecentBidInfoVO> queryRecentBidInfoListByUid(Map<String, Object> paramMap);

    ResultObject invest(Map<String, Object> paramMap);


    PaginationVO<RecentBidInfoVO> queryBidInfoByPage(Map<String, Object> paramMap);

    List<BidUser> queryBidUserTop();
}
