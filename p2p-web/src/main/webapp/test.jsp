<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <title>index</title>
</head>
<body>
<h1>平台平均年收益率为${historyAverageRate}</h1>
<h1>平台总用户数为${allUserCount}</h1>
<h1>平台累计统计金额${allBidMoney}</h1>
</body>
</html>