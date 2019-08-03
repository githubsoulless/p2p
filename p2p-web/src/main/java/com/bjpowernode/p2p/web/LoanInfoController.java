package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import javafx.scene.control.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.awt.SunHints;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private FinanceAccountService financeAccountService;
    /*
    * 分页查询产品信息
    * */
    @RequestMapping("/loan/loan")
    public String Loan(HttpServletRequest request, Model model,
                       @RequestParam(value="ptype",required = false) Integer ptype,
                       @RequestParam(value = "currentPage",required = false) Integer currentPage){

//        判断当前页是否为空，如果为空则默认当前页是第一页，从index.jsp进来时为空，
//        分页时当前页不为空
        if(null == currentPage){
            currentPage = 1;
        }

//        显示所有的产品，先将参数放到map中
        Map<String ,Object> paramMap = new HashMap<String, Object>();

/*
       如果产品类型不为空，则把产品类型放入paramMap,从index.jsp进入时类型不为空，
       需要传入类型，这时要把sql语句的where写成活的，因为在分页查询时类型是空的
       并不需要条件
*/

//      注意：前端的ptype的键和后台的键名不一致
        if(null != ptype){
            paramMap.put("productType", ptype);
        }

//        每页显示9条记录
        int pageSize = 9;

//        后台要skipNum掠过条数和pageSize每页显示条数
        int skipNum = (currentPage - 1)*pageSize;
//        此处的键要写成currentPage是为了使用通过产品类型查sql的sql语句
        paramMap.put("currentPage", skipNum);
        paramMap.put("pageSize",pageSize);
//        返回值为vo
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoListByPage(paramMap);

//        需要计算总页数，从vo里取出总条数
        int totalPage = paginationVO.getTotal().intValue()%pageSize == 0?(paginationVO.getTotal().intValue()/pageSize ):(paginationVO.getTotal().intValue()/pageSize +1 );
        model.addAttribute("totalPage",totalPage );
        model.addAttribute("dataList",paginationVO.getDataList() );
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("total", paginationVO.getTotal());

//        类型不为空的时候，把参数写进model，还是从index.jsp进来时
        if(null != ptype){
            model.addAttribute("ptype", ptype);
        }

//        投资排行榜

//        列表中的BidUser是一个新的数据类型，里面有手机号和投资金额
//        来自user和u_bid
        List<BidUser> bidUserList = bidInfoService.queryBidUserTop();
        model.addAttribute("bidUserList", bidUserList);
        return "loan";
    }


//    根据产品id查列表,(用户收益信息表和用户投标信息表)和投标产品信息表
    @RequestMapping("/loan/loanInfo")
    public String LoanInfo(HttpServletRequest request,Model model,
                           @RequestParam(value = "id",required = true) Integer id){

//     id必须有
//      根据产品id获取产品详情，返回对象
        LoanInfo loanInfo = loanInfoService.queryLoanInfoListById(id);


//     根据id获取最近投标记录，返回List,list中存放了用户收益信息表和用户投标信息表
//        两表中的值

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("loanInfoId",id);
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",10);
        List<BidInfo> bidInfoList = bidInfoService.queryRecentBidInfoListById(paramMap);

        model.addAttribute("loanInfo", loanInfo);
        model.addAttribute("bidInfoList", bidInfoList);

        //根据用户标识获取用户的帐户资金信息
//      从session中取值
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
//      判断用户是否登陆
        if(null != sessionUser){
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccount(sessionUser.getId());
            model.addAttribute("financeAccount", financeAccount);
        }

        return "loanInfo";
    }

//    分页查询
}
