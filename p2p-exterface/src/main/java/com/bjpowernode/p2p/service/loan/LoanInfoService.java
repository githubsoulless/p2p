package com.bjpowernode.p2p.service.loan;


import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {
    Double queryhistoryAverageRate();

    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap);

    LoanInfo queryLoanInfoListById(Integer id);
}
