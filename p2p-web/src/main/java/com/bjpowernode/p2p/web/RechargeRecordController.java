package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.config.AlipayConfig;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class RechargeRecordController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private RechargeRecordService rechargeRecordService;

    @RequestMapping("/loan/toAlipayRecharge")
    public String toAlipayRecharge(HttpServletRequest request, Model model,
                                   @RequestParam(value = "rechargeMoney", required = true) Double rechargeMoney) {

        System.out.println("进入到支付页面");

//            从session中取到用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

//            生成唯一的订单号，通过时间戳加redis唯一数字
        String rechargeNo = DateUtils.getTimeStamp() + redisService.getOnlyNumber();

//            生成充值订单
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
//            充值状态为0，表示正在充值中
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("支付宝充值");

        int addRechargeCount = rechargeRecordService.addRecharge(rechargeRecord);

        if (addRechargeCount > 0) {

//                调用支付宝的接口，只是给方法传递参数
            model.addAttribute("rechargeNo", rechargeNo);
            model.addAttribute("rechargeMoney", rechargeMoney);
            model.addAttribute("rechargeDesc", "支付宝充值");

        } else {
            model.addAttribute("trade_msg", "充值异常，请稍后重试");
            return "toRechargeBack";
        }
//            返回到待支付的视图
        return "p2pToPay";
//            重定向是get请求，会暴露参数
//        return "redirect:http://localhost:9090/pay/api/alipay?out_trade_no="+rechargeNo+"&total_amount="+rechargeMoney+"&subject=支付宝充值";

    }

    @RequestMapping(value = "/loan/alipayBack")
    public String alipayBack(HttpServletRequest request, Model model,
                             @RequestParam(value = "out_trade_no", required = true) String out_trade_no,
                             @RequestParam(value = "total_amount", required = true) Double total_amount) throws Exception {
        System.out.println("====支付返回====");
        Map<String, String> params = new HashMap<String, String>();

        //获取支付宝GET过来反馈信息
        Map<String, String[]> requestParams = request.getParameterMap();

        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //调用SDK验证签名,需要添加jar包和依赖
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
//        如果验签成功
        if (signVerified) {
            //准备订单查询参数
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("out_trade_no", out_trade_no);
//            调用pay工程订单查询接口——》获取订单的业务处理结果
            String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);
//            将json格式的字符串转换为json对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);


            //获取指定的key
            JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

//            获取通信标识
            String code = tradeQueryResponse.getString("code");

            //判断通信
            if (StringUtils.equals(code, "10000")) {

                //获取业务处理结果trade_status
                String tradeStatus = tradeQueryResponse.getString("trade_status");

                /*
                WAIT_BUYER_PAY	交易创建，等待买家付款
                TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
                TRADE_SUCCESS	交易支付成功
                TRADE_FINISHED	交易结束，不可退款
                */

                //如果交易状态为TRADE_CLOSED -> 更新充值记录的状态为2
                if (StringUtils.equals("TRADE_CLOSED", tradeStatus)) {
//                    更新充值记录
                    RechargeRecord rechargeRecord = new RechargeRecord();
                    rechargeRecord.setRechargeNo(out_trade_no);
                    rechargeRecord.setRechargeStatus("2");
                    int modifyRechargeCount = rechargeRecordService.modifyRechargeRecordByRechargeNo(rechargeRecord);
                    model.addAttribute("trade_msg", "充值失败");
                    return "toRechargeBack";
                } if (StringUtils.equals("TRADE_SUCCESS", tradeStatus)) {
//                 1. 更新用户账户余额 2.更新用户的充值状态为1
//                    参数：用户标识，充值金额，充值订单号
                    //从session中获取用户的信息
                    User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                    //准备充值参数
                    paramMap.put("uid", sessionUser.getId());
                    paramMap.put("rechargeMoney", total_amount);
                    paramMap.put("rechargeNo", out_trade_no);

                    RechargeRecord rechargeDetail = rechargeRecordService.queryRechargeRecordByRechardNo(out_trade_no);

//                    为什么还要获取一次状态？
                    if ("0".equals(rechargeDetail.getRechargeStatus())) {
//                        如果状态为0 ，则需要修改信息
                        int rechargeCount = rechargeRecordService.recharge(paramMap);
                        if (rechargeCount <= 0) {
                            model.addAttribute("trade_msg", "充值失败");
                            return "toRechargeBack";
                        }
                        }
                    }
                } else {
                    model.addAttribute("trade_msg", "通信失败");
                    return "toRechargeBack";
                }
            } else {
//            验证签名失败
                model.addAttribute("trade_msg", "签证签名失败");
                return "toRechargeBack";
            }
            return "redirect:/loan/myCenters";
        }

        @RequestMapping(value = "/loan/toWxpayRecharge")
        public String toWxPayRecharge(HttpServletRequest request,Model model,
                                      @RequestParam(value = "rechargeMoney",required = true)Double rechargeMoney){
            System.out.println("------toWxPayRecharge-------");

//            订单号：时间戳+redis唯一数字
            String rechargeNo = DateUtils.getTimeStamp()+redisService.getOnlyNumber();

            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);


//            生成充值记录
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setRechargeNo(rechargeNo);//订单号
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setUid(sessionUser.getId());//session中获取
            rechargeRecord.setRechargeTime(new Date());
            rechargeRecord.setRechargeStatus("0");//充值中
            rechargeRecord.setRechargeDesc("微信充值");


            int addRechargeRecordCount = rechargeRecordService.addRecharge(rechargeRecord);
            if(addRechargeRecordCount > 0){
//                 最后都是要跳转页面，充值记录添加成功则传递参数，为啥?
//                因为在二维码支付页面需要显示参数
                model.addAttribute("rechargeNo", rechargeNo);
                model.addAttribute("rechargeMoney", rechargeMoney);
                model.addAttribute("rechargeTime", new Date());

//                还要生成二维码,二维码和显示参数写在两个controller里

            }else{
                model.addAttribute("trade_msg", "充值失败，请稍后重试");
                return "toRechargeBack";
            }

            return "showQRCode";
        }

//        注意是先进入页面，在根据页面中的参数生成二维码
//        生成二维码，以流的形式,所以形参中要写响应流
    @RequestMapping(value = "/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value="rechargeNo",required = true)String rechargeNo,
                               @RequestParam(value = "rechargeMoney",required = true)String rechargeMoney
                               ) throws Exception {

        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("body", "微信支付");
        paramMap.put("out_trade_no", rechargeNo);
        paramMap.put("total_fee", rechargeMoney);
        paramMap.put("notify_url", "http://localhost:8080/p2p/loan/wxpayNotify");

//        调用pay工程的统一下单API接口，进pay工程,响应json格式的字符串
            String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);
//           将json字符串解析为json对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
//            获取return_code
            String returnCode = jsonObject.getString("return_code");
//            判断通信
        if(StringUtils.equals(Constants.SUCCESS, returnCode)){

//            通信成功，获取业务处理结果
            String resultCode = jsonObject.getString("result_code");
            if(StringUtils.equals(Constants.SUCCESS, resultCode)){
//        获取code_url
            String codeUrl = jsonObject.getString("code_url");

//            干嘛的？？
            Map<EncodeHintType,Object> hintTypeObjectMap = new HashMap<EncodeHintType,Object>();
            hintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"utf-8");
//            生成矩阵对象
            BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE,200, 200,hintTypeObjectMap);

//            定义响应流
                OutputStream outputStream = response.getOutputStream();
//        生成二维码,以流的形式
                MatrixToImageWriter.writeToStream(bitMatrix,"jpg",outputStream);

                outputStream.flush();
                outputStream.close();

            }else{
                response.sendRedirect(request.getContextPath()+"/toRechargeBack.jsp");

            }
        }else{
//            通信失败,响应页面
            response.sendRedirect(request.getContextPath()+"/toRechargeBack.jsp");
        }
//        return "";
        }
    }
