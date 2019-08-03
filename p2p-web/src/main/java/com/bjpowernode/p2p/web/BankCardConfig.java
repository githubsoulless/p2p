package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.service.loan.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BankCardConfig {

    @Autowired
    private RedisService redisService;
    @RequestMapping(value = "/test/messageCode")
    @ResponseBody
    public Object messageCode(HttpServletRequest request,
                              @RequestParam(value = "phone",required = true) String phone) throws Exception {
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
        //    调用互联网接口
        String ObjectString = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 0,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-883104</remainpoint>\\n <taskID>92959771</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                "}";
        //调用互联网接口，调用：京东万象的106短信接口
//    String jsonString = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);


//    利用fastJson解析json格式字符串
        JSONObject jsonObject = JSONObject.parseObject(ObjectString);

//    获取通信表示code
        String code = jsonObject.getString("code");


        if(StringUtils.equals("10000", code)){

            String resultXml = jsonObject.getString("result");
            Document document = DocumentHelper.parseText(resultXml);

            Node node = document.selectSingleNode("//returnstatus");

            String returnstatus = node.getText();


            if (StringUtils.equals("Success", returnstatus)) {
//
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

    @PostMapping("/test/phoneAndBankCard")
    public String checkBankCard(HttpServletRequest request, Model model,
                                @RequestParam(value = "phone",required = true) String phone,
                                @RequestParam(value = "bankCard",required = true) String bankCard,
                                @RequestParam(value = "name",required = true) String name,
                                @RequestParam(value = "idCard",required = true) String idCard) throws Exception {

        //    准备请求参数
        Map<String,Object> paramMap = new HashMap<String, Object>();

//    获取已有信息
        paramMap.put("appkey", "0ec43fc5c909d21a43382d104ee26e02");
        paramMap.put("accName", name);
        paramMap.put("certificateNo", idCard);
        paramMap.put("cardNo", bankCard);
        paramMap.put("cardPhone", phone);

        String jsonString = HttpClientUtils.doPost("https://way.jd.com/YOUYU365/keyelement", paramMap);

        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        String code = jsonObject.getString("code");
        System.out.println(code);
        if(StringUtils.equals( "10000",code)){

            String success = jsonObject.getJSONObject("result").getString("success");
            System.out.println(success);
            if(StringUtils.equals("true",success)){
                model.addAttribute("msg2", "银行卡核验成功");
            }else{
                model.addAttribute("msg2", "银行卡核验失败");
            }
        }
        return "success1";
    }
}
