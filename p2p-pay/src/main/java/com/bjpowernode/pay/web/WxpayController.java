package com.bjpowernode.pay.web;

import com.bjpowernode.http.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.awt.SunHints;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WxpayController {


//    必须有一个微信的sdk
//    用于生成code_url，这里要调用微信服务端支付系统获取code_url，响应一个xml格式的字符串，请求和响应都是
    @RequestMapping(value = "/api/wxpay")
    @ResponseBody
    public Object wxpay(HttpServletRequest request,
                        @RequestParam(value = "body",required = true)String body,
                        @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                        @RequestParam(value = "total_fee",required = true)String total_fee,
                        @RequestParam(value = "notify_url",required = true)String notify_url) throws Exception {
//        是谁调用的pay工程中的controller，并传递参数
//        body:商品描述,total_fee 用String类型，之后接口中的地址使用的是String类型
        Map<String,String> requestDataMap = new HashMap<String, String>();
        requestDataMap.put("appid", "wx8a3fcf509313fd74");//公众账号
        requestDataMap.put("mch_id","1361137902");//商户号
        requestDataMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        requestDataMap.put("body", body);
        requestDataMap.put("out_trade_no", out_trade_no);
//        关于total_fee,由于计算容易丢失精度，所以用bigdecimal
        BigDecimal bigDecimal = new BigDecimal(total_fee);//先把字符串转换为BigDecimal类型

        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));//扩大一百倍

        int i = multiply.intValue();//把这个数强制转换为int
        requestDataMap.put("total_fee", String.valueOf(i));//把这个数强制转换为字符串
        requestDataMap.put("spbill_create_ip", "127.0.0.1");//使用的是pay工程的ip地址
        requestDataMap.put("notify_url", notify_url);//传过来的还是本机地址？
        requestDataMap.put("trade_type", "NATIVE");
        requestDataMap.put("product_id", "out_trade_no");//当交易类型为native，product_id为必填项

        //所有请求参数，除了sign的非空集合，后面拼接一个key，key是商户自己生成的，再用MD5算法
        String signature = WXPayUtil.generateSignature(requestDataMap, "367151c5fd0d50f1e34a68a802d6bbca");
        requestDataMap.put("sign", signature);
        //将请求参数转换为xml格式
        String requestDataXml = WXPayUtil.mapToXml(requestDataMap);
//        将xml格式的请求参数发送给  微信系统 统一下单API接口
        String responseDataXml = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", requestDataXml);
//        将响应的xml格式的参数转换为map集合
        Map<String,String> responseDataMap = WXPayUtil.xmlToMap(responseDataXml);

        return responseDataMap;

    }


}
