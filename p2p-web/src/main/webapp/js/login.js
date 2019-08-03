var referrer = "";//登录后返回页面
referrer = document.referrer;//跳转至当前页面之前页面的URL
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});

//页面加载时执行
$(function () {
	loadStart();
$("#dateBtn1").on("click",function () {

//  根据手机号发送验证码
    var phone = $.trim($("#phone").val());

//    验证手机号和登陆密码通过，，ajax发送验证码,再进行倒计时
    if(checkphone()&&checkLoginPassword()){
        $.ajax({
            url:"loan/messageCode",
            type:"post",
            data:"phone="+phone,
            success:function (jsonObject) {
                if(jsonObject.errorMessage=="OK"){
                    alert(jsonObject.messageCode) ;
                //    进行倒计时
                    $.leftTime(60,function (d) {
                        if(d.status){
                        //    d.status倒计时是否结束
                            $("#dateBtn1").addClass("on");//重新添加一个方法on
                            $("#dateBtn1").html((d.s == "00"?"60":d.s) + "秒后重新获取");
                        }
                        else {
                            $("#dateBtn1").removeClass("on");
                            $("#dateBtn1").html("获取短信验证码");
                        }
                    });
                }else{
                    $("#showId").html("短信异常，请稍后重试");
                }


            },
            error:function () {
                $("#showId").html("短信异常，请稍后重试");
            }
        });
    }

});
});

function loadStart() {
	$.ajax({
		url:"loan/loadStart",
		type:"get",
		success:function (jsonObject) {
			$(".historyAverageRate").html(jsonObject.historyAverageRate);
			$("#allUserCount").html(jsonObject.allUserCount);
			$("#allBidMoney").html(jsonObject.allBidMoney);
		}
	});
}

//验证手机号
function checkphone() {
	var phone = $.trim($("#phone").val());

//	验证手机号是否存在
	if ("" == phone) {
		$("#showId").html("请输入手机号码");
		return false;
	}
//	验证手机号格式是否正确
	else if (!/^1[1-9]\d{9}$/.test(phone)) {
		$("#showId").html("请输入正确的手机号码");
		return false;
	}
//	都正确清空提示框
	else {
		$("#showId").html("");
	}
	return true;
}

//验证登陆密码
function checkLoginPassword() {
//	验证是否为空
	var loginPassword = $.trim($("#loginPassword").val());

	if ("" == loginPassword) {
		$("#showId").html("请输入登录密码");
		return false;
	} else {
		$("#showId").html("");
	}

	return true;
}

//验证验证码
function checkCaptcha() {
	var captcha = $.trim($("#captcha").val());
	var flag = false;

	if ("" == captcha) {
		$("#showId").html("请输入图形验证码");
		return false;
	} else {
		$.ajax({
			url:"loan/checkCaptcha",
			type:"post",
			data:"captcha="+captcha,
			async:false,//同步
			success:function (jsonObject) {
				if (jsonObject.errorMessage == "OK") {
					$("#showId").html("");
					flag = true;
				} else {
					$("#showId").html(jsonObject.errorMessage);
					flag = false;
				}
			},
			error:function () {
				$("#showId").html("系统繁忙，请稍后重试");
				flag = false;
			}
		});
	}

	if (!flag) {
		return false;
	}
	return true;
}

function checkMessageCode() {
	var messageCode = $.trim($("#messageCode").val());

	if ("" == messageCode) {
		$("#showId").html("请输入短信验证码");
		return false;
	} else {
		$("#showId").html("");
	}
	return true;
}

//验证登陆
function login() {
	var phone = $.trim($("#phone").val());
	var loginPassword = $.trim($("#loginPassword").val());
	var messageCode = $.trim($("#messageCode").val());

	if(checkphone()&&checkLoginPassword()&&checkMessageCode()){
		//在每个方法中已经验证过空值
		$("#loginPassword").val($.md5(loginPassword));
		$.ajax({
			url:"loan/login",
			type:"post",
			data:"phone="+phone+"&loginPassword="+$.md5(loginPassword)+"&messageCode="+messageCode,
			success:function (jsonObject) {
				if(jsonObject.errorMessage == "OK"){
					alert(referrer)
				//	成功，跳转到登陆之前的页面
					if(""==referrer){
						window.location.href="index";//登陆前就是首页面
					}else{
						window.location.href=referrer;
					}
				}else{
				//	登陆信息有误
					$("#showId").html("用户名或密码有误，请重新输入")
				}
			},
			error:function () {
				$("#showId").html("系统繁忙，请稍后重试");
			}

		});
	}
}
