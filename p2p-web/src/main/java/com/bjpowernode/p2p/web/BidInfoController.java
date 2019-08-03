package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.RecentBidInfoVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BidInfoController {
    @Autowired
  private BidInfoService bidInfoService;


    @RequestMapping(value = "/loan/invest")
    public @ResponseBody Object invest(HttpServletRequest request,
                  @RequestParam (value = "loanId",required = true) Integer loanId,
                  @RequestParam (value = "bidMoney",required = true) Double bidMoney) {
        Map<String,Object> retMap = new HashMap<String, Object>();

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //准备投资参数
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("loanId",loanId);
        paramMap.put("bidMoney",bidMoney);
        paramMap.put("phone", sessionUser.getPhone());

        //用户投资(用户标识，产品标识，投资金额) -> 业务处理结果
        ResultObject resultObject = bidInfoService.invest(paramMap);

        if (StringUtils.equals(Constants.SUCCESS, resultObject.getErrorCode())) {
            retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
        } else {
            retMap.put(Constants.ERROR_MESSAGE,"投资失败");
            return retMap;
        }

        return retMap;
    }


//    分页查询 全部投资
    @RequestMapping(value = "/loan/myInvest")
    public String myInvest(HttpServletRequest request, Model model,
                           @RequestParam (value = "currentPage",required = false) Integer currentPage) {

        if (null == currentPage) {
            currentPage = 1;
        }

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //准备分页查询参数
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("uid",sessionUser.getId());
        int pageSize = 10;
        paramMap.put("currentPage",(currentPage-1)*pageSize);
        paramMap.put("pageSize",pageSize);

        //根据用户标识分页查询投资记录(用户标识，页码，每页显示条数) -> 返回：每页显示数据和总记录数
        PaginationVO<RecentBidInfoVO> paginationVO = bidInfoService.queryBidInfoByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }


        model.addAttribute("totalPage",totalPage);
        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("dataList",paginationVO.getDataList());
        model.addAttribute("currentPage",currentPage);



        return "myInvest";
    }


}
