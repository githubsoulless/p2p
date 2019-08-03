<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>银行卡核验</title>
    <script language="javascript" type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <script language="javascript" type="text/javascript" src="${pageContext.request.contextPath}/js/jQuery.md5.js"></script>
    <script language="javascript" type="text/javascript" src="${pageContext.request.contextPath}/js/leftTime.min.js"></script>
    <style type="text/css">
        .testBtn-a{display: inline-block;height:30px;line-height:30px;padding:0 8px; border:0; border-radius:5px;color:#fff;background:rgb(65,133,244);cursor: pointer;}
        .testBtn-a.on{background:#c9c9c9;color:#666;cursor: default;}
    </style>
    <script type="text/javascript">
       function checkMessage() {


//  根据手机号发送验证码
           var phone = $.trim($("#phone").val());
           $.ajax({
               url: "test/messageCode",
               type: "post",
               data: "phone=" + phone,
               success: function (jsonObject) {
                   if (jsonObject.errorMessage == "OK") {
                       alert(jsonObject.messageCode);
                       //    进行倒计时
                       $.leftTime(60, function (d) {
                           if (d.status) {
                               //    d.status倒计时是否结束
                               $("#dateBtn1").addClass("on");//重新添加一个方法on
                               $("#dateBtn1").html((d.s == "00" ? "60" : d.s) + "秒后重新获取");
                           } else {
                               $("#dateBtn1").removeClass("on");
                               $("#dateBtn1").html("获取短信验证码");
                           }
                       });
                   } else {
                       $("#showId").html("短信异常，请稍后重试");
                   }


               },
               error: function () {
                   $("#showId").html("短信异常，请稍后重试");
               }
           });
       }

    </script>
</head>
<body>
<form action="${pageContext.request.contextPath}/test/phoneAndBankCard" method="post">
    请输入手机号<input type="text" name="phone"><br>
    <div class="login-yzm">
    <div id="a" class="yzm-box" style="display:block;">
        <input id="messageCode" type="text" class="yzm" placeholder="获取短信验证码"/>
        <a style='cursor:pointer;'>
            <button type="button" class="testBtn-a" id="dateBtn1" onclick="checkMessage();">倒计时开始</button>
        </a>
    </div>


</div>
    <br>
    请输入银行卡号<input type="text" name="bankCard">
    <br>
    请输入姓名<input type="text" name="name">
    <br>
    请输入身份证号<input type="text" name="idCard">
    <input type="submit" value="提交" >
</form>


</body>
</html>