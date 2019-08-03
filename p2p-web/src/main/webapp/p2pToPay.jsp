<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <title>p2p调用pay工程的支付请求页面工程</title>
</head>
<body>
<%--用来传递参数，防止调用时暴露参数--%>
<form method="post" action="http://localhost:9090/pay/api/alipay">
    <input type="hidden" name="out_trade_no" value="${rechargeNo}"/>
    <input type="hidden" name="total_amount" value="${rechargeMoney}"/>
    <input type="hidden" name="subject" value="${rechargeDesc}"/>
</form>
<%--表单自动提交--%>
<script>document.forms[0].submit();</script>
</body>
</html>