<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>动力金融网-CFCA认证的互联网金融公司</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/trafficStatistics.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/share.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css" />

</head>

<body>
<div id="header">
<jsp:include page="commons/header.jsp"/>
</div>

<!--散标投资 begin-->
<div id="sbtz" class="invest-details">

<!--页中begin-->
<div class="mainBox pro-details-body">
  <div class="homeWap clearfix" id="huacengPar">
    <div class="pro-details-left">
      
      <!-- 产品详情start -->
      <div class="pro-info-details">
        <div class="pro-name">
          <h2><span>${loanInfo.productName}</span>${loanInfo.productNo}</h2>
        </div>
        <div class="pro-info">
          <ul class="clearfix">
            <li class="info-1">
              <p>历史年化利率</p>
              <h3>${loanInfo.rate}<span>%</span></h3>
              <div class="info-bt">
              <span>本产品采用普通利率</span>
              </div>
            </li>
            <li class="info-2">
              <p>募集金额(元)</p>
              <h3>${loanInfo.productMoney}</h3>
              <div class="info-bt">
              	<span>
              		<!-- 只有状态为0时或剩余可投金额为0为募集中，其它都为未满标 -->
                    <c:choose>
                        <c:when test="${loanInfo.leftProductMoney eq 0}">已满标</c:when>
                        <c:otherwise>募集中 | 剩余募集金额 ${loanInfo.leftProductMoney}元</c:otherwise>
                    </c:choose>

              		<!-- 已满标 -->
              	</span>
              </div>
            </li>
            <li class="info-3">
              <p>投资周期</p>
              <!-- 只有新手宝产品周期为天 -->
                <c:choose>
                    <c:when test="${loanInfo.productType eq 0}">
                        <h3>${loanInfo.cycle}<span>天</span></h3>
                    </c:when>
                    <c:otherwise>
                        <h3>${loanInfo.cycle}<span>个月</span></h3>
                    </c:otherwise>
                </c:choose>

              <div class="info-bt"><span></span></div>
            </li>
          </ul>
        </div>
        <dl class="pro-syfs">
          <dt><span>收益获取方式</span></dt>
          <dd><span>收益返还：</span>到期还本付息</dd>
        </dl>
      </div>
      <!-- 产品详情end -->
      
      <!-- 投资记录start -->
      <div class="pro-details-cnt">
        <ul class="tabNav clearfix">
          <li><a id="one3" href="javascript:void(0);" class="s">投资记录</a></li>
        </ul>
        
        <div class="invest-record" id="con_one_3" style="display:block">
        <div class="vertical-side">投资列表</div>
		<dl class="record-list">
		<dt>
			<span class="record-num">序号</span>
            <span class="invest-user">投资人</span>
            <span class="invest-money">投资金额(元)</span>
            <span class="invest-time">投资时间</span>
		</dt>
		
			<!-- 如果投资记录为空，显示以下文字 -->
            <c:choose>
                <c:when test="${empty bidInfoList}">
                    <dd style="text-align:center;">该产品暂时还没有人投资，赶快去投资吧~</dd>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${bidInfoList}" var="bidInfo" varStatus="index">
                        <dd>
                            <%--投资数量用varStatus--%>
                            <span class="record-num">${index.count}</span>
                            <%--取字串左闭右开--%>
                            <span class="invest-user">${fn:substring(bidInfo.user.phone,0,3)}******${fn:substring(bidInfo.user.phone,9,11)}</span>
                            <span class="invest-money">${bidInfo.bidMoney}</span>
                            <span class="invest-time"><fmt:formatDate value="${bidInfo.bidTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                        </dd>
                    </c:forEach>

                </c:otherwise>
            </c:choose>



		</dl>
		</div>
      </div>
      <!-- 投资记录end -->
    </div>
    
    <!--页面右侧begin-->
    <div class="pro-details-right">
      <div class="right-calculator" id="huaceng">
        <div class="calculator-cnt">
          <h2>立即投资</h2>
          <dl class="profits-mode">
            <dt>收益获取方式</dt>
            <dd class="clearfix"><span id="fanhuan"><em>到期还本付息</em></span></dd>
          </dl>
          <dl class="usable">
            <dt>我的账户可用</dt>
            <dd>资金(元)：
                <!-- 判断用户是否登录：未登录，显示登录连接 -->

                <!-- 判断用户是否登录：已登录，显示可用余额 -->
                <c:choose>
                    <c:when test="${empty user}">
                        <span style="font-size:18px;color:#ff6161;vertical-align:bottom;"><a href="${pageContext.request.contextPath}/login.jsp">请登录</a></span>

                    </c:when>
                    <c:otherwise>
                        <span style="font-size:18px;color:#ff6161;vertical-align:bottom;"><fmt:formatNumber value="${financeAccount.availableMoney}" type="currency"/> </span>

                    </c:otherwise>
                </c:choose>


            </dd>
          </dl>
          <div class="expect-box">
            <div class="expect-money">预计本息收入(元)：<span id="shouyi" class="money"></span><span class="prompt" style="display:block;">请在下方输入投资金额</span></div>
            <input type="text" id="bidMoney" name="bidMoney" onblur="checkMoney();" placeholder="请输入投资金额，应为100元的整倍数" maxlength="9"/>
            <div class="max-invest-money"></div>
          </div>
          <div class="invest-btn">
          	<a id="investNow" href="javascript:void(0)" class="btn-1" onclick="invest();">立即投资</a>
          </div>
          <input type="hidden" id="loanId" name="loanId" value="${loanInfo.id}"/>
        </div>
      </div>
    </div>
    <!--页面右侧end-->
  </div>
</div>
<!--页中end-->

</div>
<!--散标投资 end-->

<!--遮罩层-->
<div class="dialog-overlay" id="dialog-overlay1" style="display:none;"></div>

<!--投资成功浮层start-->
<div class="layer-body failureSuccess failurePayment" id="failurePayment" style="display:none;width:500px;height:100px;top:75%;">
  <a class="layer-close" href="javascript:closeit();"></a>
  <div style="background:#f2f2f2; line-height:105px;text-align:center;"><font style="font-size:25px;">投资成功</font></div>
</div>
<!--投资成功浮层end-->

<!--页脚start-->
<jsp:include page="commons/footer.jsp"/>
<!--页脚end-->

<script type="text/javascript">
function closeit() {
	$("#failurePayment").hide();
	$("#dialog-overlay1").hide();
	window.location.href="${pageContext.request.contextPath}/loan/myCenter";
}
//投资金额框中，失去焦点后对输入框中的内容进行验证
    function checkMoney() {
        var bidMoney = $.trim($("#bidMoney").val())
        var productType = "${loanInfo.productType}";
        var rate = "${loanInfo.rate}";
        var cycle = "${loanInfo.cycle}"



        if("" == bidMoney){
        //    提示框被隐藏在输入框下面
            $(".max-invest-money").html("请输入投资金额");
            return false;
        }
    //    js中NaN数据类型是  不是 一个数字
        else if(isNaN(bidMoney)){
            $(".max-invest-money").html("投资金额必须为数字");
            return false;
        }else if(bidMoney <= 0){
            $(".max-invest-money").html("投资金额必须为大于0的整数");
            return false;
        }else if(bidMoney % 100 != 0){
            $(".max-invest-money").html("投资金额必须为100的整数倍");
            return false;
        }else{
            $(".max-invest-money").html("");

        //    收益金额 = 投资金额 * 年利率

        //    收益金额 = 投资金额 * 日利率 * 天数

        //    id为shouyi

            var incomeMoney ="";
            if(0 == productType){
            //    产品类型为新手宝，周期为天
                incomeMoney = bidMoney * (rate /100 /365) * cycle;
            //    金额保留小数点后两位,对金额进行扩大100倍，取整，再缩小100北
            }else if(1 == productType){
            //    优选或散标，周期为月
                incomeMoney = bidMoney * (rate /100 /365) * cycle * 30;


            }
            incomeMoney = Math.round(incomeMoney*Math.pow(10,2))/Math.pow(10,2);

            $("#shouyi").html(incomeMoney);
        }
        return true;
    }


//    发起投资请求
var contextPath = "${pageContext.request.contextPath}";
    function invest() {
    //    判断用户是否登陆
        var bidMoney = $.trim($("#bidMoney").val());
        var user = "${user}";
        var name = "${user.name}";
        var loanId = "${loanInfo.id}";
        var bidMinLimit = "${loanInfo.bidMinLimit}";
        var bidMaxLimit = "${loanInfo.bidMaxLimit}";
        var leftProductMoney = "${loanInfo.leftProductMoney}";
        var availableMoney = "${financeAccount.availableMoney}";
        if(checkMoney()){
            if("" == user){
                window.location.href = contextPath+"/login.jsp";
                return false;
            }
        //    判断是否进行实名认证
            else if("" == name){
                window.location.href = contextPath+"/realName.jsp";
                return false;
            }else if(parseFloat(bidMoney) < parseFloat(bidMinLimit)) {
                $(".max-invest-money").html("投资金额不得低于" + bidMinLimit + "元");
                return false;
            } else if (parseFloat(bidMoney) > parseFloat(bidMaxLimit)) {
                $(".max-invest-money").html("投资金额不得高于" + bidMaxLimit + "元");
                return false;
            } else if (parseFloat(bidMoney) > parseFloat(leftProductMoney)) {
                $(".max-invest-money").html("投资金额不得超过产品剩余可投金额");
                return false;
            } else if (parseFloat(bidMoney) > parseFloat(availableMoney)) {
                $(".max-invest-money").html("帐户余额不足");
                return false;
            }else {
                $(".max-invest-money").html("");

                $.ajax({
                    url:contextPath + "/loan/invest",
                    type:"post",
                    data:"loanId="+loanId+"&bidMoney="+bidMoney,
                    success:function (jsonObject) {
                        if (jsonObject.errorMessage == "OK") {
                            $("#failurePayment").show();
                            $("#dialog-overlay1").show();
                        } else {
                            $(".max-invest-money").html("投资人数过多，请稍后再试");
                        }

                    },
                    error:function () {
                        $(".max-invest-money").html("投资人数过多，请稍后再试");

                    }
                });

             }
        }
    }
</script>
</body>
</html>