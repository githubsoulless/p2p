package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.RecentBidInfoVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.*;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private BidInfoService bidInfoService;
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private IncomeRecordService incomeRecordService;

    @RequestMapping("/loan/checkPhone")
    @ResponseBody
    public Object checkPhone(@RequestParam(value = "phone",required = true) String phone){

//        这里要根据手机号验证用    到user表中查，如果有则返回user
        User user = userService.queryUserByPhone(phone);
        Map<String,Object> retMap = new HashMap<String, Object>();
//        user有什么用呢?
        if(null == user){
            retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
        }else{
            retMap.put(Constants.ERROR_MESSAGE, "该手机号码已存在，请更换手机号码");
        }
        return retMap;
    }
//    验证码
    @PostMapping(value = "/loan/checkCaptcha")
    @ResponseBody
    public Map<String,Object> checkCaptcha(HttpServletRequest request,
            @RequestParam(value = "captcha",required = true) String captcha){

        Map<String,Object> retMap = new HashMap<String, Object>();
//        比较传入的验证码图片的参数是否和session中已有的验证码一致

//        取得session中的验证码
        String sessionCaptcha = (String) request.getSession().getAttribute(Constants.CAPTCHA);
        if(!StringUtils.equalsIgnoreCase(sessionCaptcha, captcha)){
            retMap.put(Constants.ERROR_MESSAGE, "请输入正确的验证码");
            return retMap;
        }
        retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
        return retMap;
    }


//    验证注册，跳转到真实姓名验证,并且新增用户信息和用户账户信息
    @GetMapping("/loan/register")
    @ResponseBody
    public Map<String,Object> register(HttpServletRequest request,
                                       @RequestParam(value = "phone",required = true) String phone,
                                       @RequestParam(value = "loginPassword",required = true) String loginPassword){
        Map<String,Object> retMap = new HashMap<String,Object>();

        ResultObject resultObject = userService.register(phone,loginPassword);

//        判断用户是否注册成功
        if(StringUtils.equals(resultObject.getErrorCode(), Constants.SUCCESS)){
//        如果成功，将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(phone));
            retMap.put(Constants.ERROR_MESSAGE, Constants.OK);

        }else{

//            如果失败，将失败信息放到map中
            retMap.put(Constants.ERROR_MESSAGE, "注册失败，请重试");
            return retMap;
        }

        return retMap;
    }


//获取小窗口中的用户余额
    @RequestMapping(value = "/loan/myCenter")
    @ResponseBody
    public FinanceAccount myCenter(HttpServletRequest request){

//        先从session中获取user信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
//        通过用户id查询账户
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccount(sessionUser.getId());

        return financeAccount;
    }


//      进行实名认证
    @PostMapping("/loan/verifyRealName")
    @ResponseBody
    public Map<String,Object> verifyRealName(HttpServletRequest request,
                                                @RequestParam(value="idCard",required=true) String idCard,
                                                @RequestParam(value = "realName",required = true) String realName)throws Exception{
    Map<String,Object> retMap = new HashMap<String, Object>();

//    准备请求参数
    Map<String,Object> paramMap = new HashMap<String, Object>();

//    获取已有信息
        paramMap.put("appkey", "0ec43fc5c909d21a43382d104ee26e02");
        paramMap.put("cardNo", idCard);
        paramMap.put("realName", realName);
//    调用互联网实名认证接口，京东万象平台的身份证二要素接口进行验证
        String jsonString = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);

//        备用测试
//       String jsonString = "{\"code\":\"10000\",\"charge\":false,\"remain\":1305,\"msg\":\"查询成功\",\"result\":{\"error_code\":0,\"reason\":\"成功\",\"result\":{\"realname\":\"乐天磊\",\"idcard\":\"350721197702134399\",\"isok\":true}}}";

//        用fastjson工具来解析，把json格式的字符串解析为json对象
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

//        获取通信标识,测试是否连接成功
        String code = jsonObject.getString("code");
        if(StringUtils.equals( "10000",code)){

//            获取是否匹配
//            从jsonObject对象中获取对象
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

            if(isok){
//                成功，返回正确信息,更新用户表中身份证信息和姓名，以及最近登陆时间
                User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                User user = new User();
                user.setIdCard(idCard);
                user.setName(realName);
                user.setId(sessionUser.getId());

                int modifyUserCount = userService.modifyUserById(user);
                if(modifyUserCount > 0){
//                    把更新信息保存到session中，返回成功信息
                    retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
                    sessionUser.setIdCard(idCard);
                    sessionUser.setName(realName);
                    request.getSession().setAttribute(Constants.SESSION_USER,sessionUser);
                }
            }else{
                retMap.put(Constants.ERROR_MESSAGE, "真实姓名和身份证不匹配");
                return retMap;
            }
        }else{
            retMap.put(Constants.ERROR_MESSAGE, "通信异常");
            return retMap;
        }

    return retMap;
    }


//登陆页
@GetMapping("/loan/loadStart")
@ResponseBody
public Object loadStart(){

    Map<String,Object> retMap = new HashMap<String,Object>();


    //历史平均年化收益率
    Double historyAverageRate = loanInfoService.queryhistoryAverageRate();

    //平台注册总人数
    Long allUserCount = userService.queryAllUserCount();

    //平台累计投资金额
    Double allBidMoney = bidInfoService.queryAllBidMoney();

    retMap.put(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
    retMap.put(Constants.ALL_USER_COUNT,allUserCount);
    retMap.put(Constants.ALL_BID_MONEY,allBidMoney);

    return retMap;

}


//验证登陆
    @PostMapping("/loan/login")
    @ResponseBody
    public Object login(HttpServletRequest request,
                                    @RequestParam(value = "loginPassword",required = true) String loginPassword,
                                    @RequestParam(value = "phone",required = true) String phone,
                                    @RequestParam(value = "messageCode",required = true)String messageCode){
        Map<String,Object> retMap = new HashMap<String,Object>();

//      从redis中获取短信验证码
        String redisMessageCode = redisService.get(phone);

//     判断用户验证码是否正确
        if(StringUtils.equals(messageCode, redisMessageCode)){

            //        根据电话和登陆密码查询用户，更新最近登陆时间，返回user
            User user = userService.login(loginPassword,phone);
            if(null == user){
                retMap.put(Constants.ERROR_MESSAGE, "用户名或密码错误");
                return retMap;
            }else{
                request.getSession().setAttribute(Constants.SESSION_USER, user);
//            session中的最近登陆时间不是本次的登陆时间
                retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
                return retMap;
            }
        }

        return retMap;

    }

//    登出
    @RequestMapping("/loan/logout")
    public String logout(HttpServletRequest request){

//        让session失效，清楚session中的user

        request.getSession().invalidate();
        request.getSession().removeAttribute(Constants.SESSION_USER);
//        重定向到首页面
        return "redirect:/index";
    }

//    短信验证码
@RequestMapping(value = "/loan/messageCode")
@ResponseBody
public Object messageCode(HttpServletRequest request,
                              @RequestParam(value = "phone",required = true) String phone) throws DocumentException {
    Map<String, Object> retMap = new HashMap<String, Object>();

    //生成一个随机数字
    String messageCode = getRandomNumber(6);

    //准备短信验证码的短信内容 = 短信签名 + 短信正文(是需要审核)
    String content = "【凯信通】您的验证码是：" + messageCode;

    //准备接口参数
    Map<String, Object> paramMap = new HashMap<String, Object>();

    //申请的appkey
    paramMap.put("appkey", "29feced2020768bf170ecb24");

    //手机号码
    paramMap.put("mobile", phone);

    //短信内容
    paramMap.put("content", content);


    //调用互联网接口，调用：京东万象的106短信接口
//    String jsonString = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);

//    调用互联网接口
    String ObjectString = "{\n" +
            "    \"code\": \"10000\",\n" +
            "    \"charge\": false,\n" +
            "    \"remain\": 0,\n" +
            "    \"msg\": \"查询成功\",\n" +
            "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-883104</remainpoint>\\n <taskID>92959771</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
            "}";
//    利用fastJson解析json格式字符串
    JSONObject jsonObject = JSONObject.parseObject(ObjectString);

//    获取通信表示code
    String code = jsonObject.getString("code");

//    判断通信是否成功
    if(StringUtils.equals("10000", code)){
//      获取result，把xml格式转为字符串
        String resultXml = jsonObject.getString("result");
        Document document = DocumentHelper.parseText(resultXml);

        //该节点的xpath路径：/returnsms/returnstatus 或者 //returnstatus
        Node node = document.selectSingleNode("//returnstatus");

        //获取该节点的文本内容
        String returnstatus = node.getText();

        //判断短信是否发送成功
        if (StringUtils.equals("Success", returnstatus)) {
//            把生成的随机验证码放到缓存中
            redisService.put(phone, messageCode);
            retMap.put("messageCode",messageCode );
            retMap.put(Constants.ERROR_MESSAGE, Constants.OK);
        }else {
            retMap.put(Constants.ERROR_MESSAGE,"通信异常，请稍后重试");
            return retMap;
        }
    }else {
        retMap.put(Constants.ERROR_MESSAGE,"通信异常，请稍后重试");
        return retMap;
    }

    return retMap;

}
    private String getRandomNumber(int count) {

        String[] arr = {"0","1","2","3","4","5","6","7","8","9"};

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < count; i++) {
            int index = (int) Math.round(Math.random()*9);
            sb.append(arr[index]);
        }
        return sb.toString();
    }

//我的小金库
    @RequestMapping(value = "/loan/myCenters")
    public String myCenter(HttpServletRequest request, Model model){

        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

//       根据用户标识获取账户资金信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccount(sessionUser.getId());

        model.addAttribute("financeAccount", financeAccount);

//        把一下查询记录看作一个分页

//        需要传入当前页和每页显示条数
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("uid", sessionUser.getId());
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",5);
//        根据用户标识查最近投资记录
        //根据用户标识获取最近投资记录,显示第1页，每页显示5条
        List<RecentBidInfoVO> recentBidInfoVOList = bidInfoService.queryRecentBidInfoListByUid(paramMap);
        model.addAttribute("recentBidInfoVOList",recentBidInfoVOList);

//        充值记录

        List<RechargeRecord> rechargeRecordList = rechargeRecordService.queryRecentRechargeRecordListByUid(paramMap);
        model.addAttribute("rechargeRecordList", rechargeRecordList);
//        收益记录
        List<IncomeRecord> incomeRecordList = incomeRecordService.queryRecentIncomeRecordListByUid(paramMap);
        model.addAttribute("incomeRecordList", incomeRecordList);
        return "myCenter";
    }
}