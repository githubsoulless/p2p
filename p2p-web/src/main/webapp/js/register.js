


//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}


//验证电话号码
function checkPhone() {
    var phone = $.trim($("#phone").val())
    var flag = true;

    if("" == phone){
        showError("phone","电话号码不能为空")
        return false;
    }else if(!/^1[1-9]\d{9}$/.test(phone)){

        //手机号必须数字0-9，正则表达式
        showError("phone","请输入正确的手机号码");
        return false;

    }else{
    //    进行后台验证，如果手机号存在则提示用户手机号已存在，return false
        $.ajax({
            url:"loan/checkPhone",
            type:"get",
            data:"phone="+phone,
            //同步请求
            async:false,
            //后台要返回数据jsonObject,errorMessage里面放了什么？
            success:function (jsonObject) {
                if(jsonObject.errorMessage == "OK"){
                    showSuccess("phone")
                    flag=true;
                }else{
                    showError("phone",jsonObject.errorMessage);
                    flag = false
                }

            },
            error:function () {
                showError("phone","系统繁忙，请稍后再试")
                flag=false;
            }

        });
    //    否则通过验证打绿色的勾，可以继续向下执行,方法结束，返回！！
    //    将flag的值变为Boolean类型

    }
    return flag;
}

//检查登陆密码
function checkLoginPassword() {
    //检查是否为空
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());


    if(""==loginPassword){
        showError("loginPassword","登陆密码不能为空");
        return false;
    }else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
        showError("loginPassword","密码字符只可使用数字和大小写英文字母");
        return false;
    } else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
        showError("loginPassword","密码应同时包含英文和数字");
        return false
    } else if (loginPassword.length < 6 || loginPassword.length > 20) {
        showError("loginPassword", "密码长度应为6-20位");
        return false;
    } else {
        showSuccess("loginPassword");
    }

    //这步是为了防止用户先写确认密码，再写登陆密码，所以加一个判断
    if (replayLoginPassword != loginPassword) {
        showError("replayLoginPassword","两次输入登录密码不一致");
    }
//该方法不需要后台验证，所以只需要跳出循环即可
    return true;
}

//验证确认密码
function checkLoginPasswordEqu() {
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());

    //必须有登陆密码才能有确认密码，所以要强制判断登陆密码是否为空
    //但是不需要判断登陆密码是否正确
    if ("" == loginPassword) {
        showError("loginPassword","请输入登录密码");
        return false;
    } else if ("" == replayLoginPassword) {
        showError("replayLoginPassword","请再次输入确认登录密码");
        return false;
    } else if (replayLoginPassword != loginPassword) {
        showError("replayLoginPassword", "两次输入登录密码不一致");
        return false;
    } else {
        showSuccess("replayLoginPassword");
    }

    return true;
}

//验证图片验证码
function checkCaptcha() {
    var captcha = $.trim($("#captcha").val());
    var flag = true;
    if("" == captcha){
        showError("captcha","验证码不能为空");
        return false;
    }else{
        $.ajax({
            url:"loan/checkCaptcha",
            type:"post",
            data:{
                "captcha":captcha
            },
            async:false,
            //验证码是否输入正确，会返回一个值
            success:function (jsonObject) {
                if(jsonObject.errorMessage == "OK"){
                    showSuccess("captcha");
                    flag = true;
                }else{
                    showError("captcha",jsonObject.errorMessage);
                    flag = false;
                }
            },
            error:function () {
                showError("captcha","系统繁忙，请稍后再试");
                flag = false;
            }
        });
    }
    if(!flag){
        return false;
    }
    return true;
}

//注册
function register() {
//    获取所有的参数，不需要验证码
//    需要把电话和密码添加到user表中
//    需要给密码和确认密码添加md5
    var phone = $.trim($("#phone").val());
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());
    if(checkPhone() && checkLoginPassword() && checkLoginPasswordEqu() && checkCaptcha()){
    //    条件满足，则给两个密码进行加密,使用js中自带的加密方法即可
    //    注意这步只对文本框中的内同进行加密，并没有对传递的参数进行加密

        $("#loginPassword").val($.md5(loginPassword));
        $("#replayLoginPassword").val($.md5(replayLoginPassword));

    //    发送ajax请求，把数据添加到数据库
        $.ajax({
            url:"loan/register",
            type:"get",//密码已经加密
            data:"phone="+phone+"&loginPassword="+$.md5(loginPassword),
            async:false,//同步请求
            //如果成功则跳转到真实姓名验证
            success:function (jsonObject) {

                if(jsonObject.errorMessage == "OK"){
                    alert(jsonObject.errorMessage)
                    window.location.href = "realName.jsp";
                }else{
                    showError("captcha","系统繁忙，请稍后重试")
                }
            },
            error:function () {
                showError("captcha","系统繁忙，请稍后重试");

            }
        });
    }
}