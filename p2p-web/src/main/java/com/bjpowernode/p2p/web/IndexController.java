package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private UserService userService;

    //用户投标信息
    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model){

        //历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryhistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE, historyAverageRate);


        //平台注册总人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT, allUserCount);


        //平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY, allBidMoney);



        /*
        * 分页获取产品信息列表，根据产品类型，需要传入的参数是
        * 产品类型，当前页，显示条数
        * */
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("currentPage", 0);

        /*
        * 获取新手宝，产品类型为0,显示1条记录
        * */
        paramMap.put("productType",Constants.PRODUCT_TYPE_X);
        paramMap.put("pageSize", 1);
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList", xLoanInfoList);


        /*
        * 获取优选宝，产品类型为1，显示4条记录
        * */

        paramMap.put("productType", Constants.PRODUCT_TYPE_U);
        paramMap.put("pageSize", 4);
        List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList", uLoanInfoList);


        /*
        * 获取散标产品：产品类型为2，显示8条记录
        * */

        paramMap.put("productType", Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize", 8);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList", sLoanInfoList);

        return "index";
    }

}
