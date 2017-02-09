<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<html>
<head>
    <title>futureMe.xin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/main.css"/>
    <script type="text/javascript" src="http://www.js-css.cn/jscode/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/laydate/laydate.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/jquery.cookie.js" type="text/javascript" charset="utf-8"></script>
    <script>
        window.onload = function () {
            var subjectElement = document.getElementById("subject");
            var date = new Date();
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            subjectElement.value = "一封来自" + year + "年" + month + "月" + day + "日的信";

        };

        function clearOnClickEmailInput() {
            var recipientElement = document.getElementById("recipient");
            if (recipientElement.value == "E-mail地址") {
                recipientElement.value = "";
            }
        }
        //发送验证码时添加cookie
        function addCookie(name,value,expiresHours){
            var cookieString=name+"="+escape(value);
            //判断是否设置过期时间,0代表关闭浏览器时失效
            if(expiresHours>0){
                var date=new Date();
                date.setTime(date.getTime()+expiresHours*1000);
                cookieString=cookieString+";expires=" + date.toUTCString();
            }
            document.cookie=cookieString;
        }
        //修改cookie的值
        function editCookie(name,value,expiresHours){
            var cookieString=name+"="+escape(value);
            if(expiresHours>0){
                var date=new Date();
                date.setTime(date.getTime()+expiresHours*1000); //单位是毫秒
                cookieString=cookieString+";expires=" + date.toGMTString();
            }
            document.cookie=cookieString;
        }
        //根据名字获取cookie的值
        function getCookieValue(name){
            var strCookie=document.cookie;
            var arrCookie=strCookie.split("; ");
            for(var i=0;i<arrCookie.length;i++){
                var arr=arrCookie[i].split("=");
                if(arr[0]==name){
                    return unescape(arr[1]);
                    break;
                }else{
                    return "";
                    break;
                }
            }

        }
        $(function(){
            $("#sendVCode").click(function (){
                sendCode($("#sendVCode"));
            });
            v = getCookieValue("sendVCodesremained");//获取cookie值
            if(v>0){
                settime($("#sendVCode"));//开始倒计时
            }
        });
        //发送验证码
        function sendCode(obj){
            var recipient = $("#recipient").val();
            var result = isValidMailAddress();
            if(result){
                doPostBack('${base}/letter/sendVCode',backFunc1,{"recipient":recipient});
                addCookie("secondsremained",60,60);//添加cookie记录,有效时间60s
                settime(obj);//开始倒计时
            }
        }
        //将手机利用ajax提交到后台的发短信接口
        function doPostBack(url,backFunc,queryParam) {
            $.ajax({
                async : false,
                cache : false,
                type : 'POST',
                url : url,// 请求的action路径
                data:queryParam,
                error : function() {// 请求失败处理函数
                },
                success : backFunc
            });
        }
        function backFunc1(data){
            var d = $.parseJSON(data);
            if(!d.success){
                alert(d.msg);
            }else{//返回验证码
                alert(d.msg);
//                $("#code").val(d.msg);
            }
        }
        //开始倒计时
        var countdown;
        function settime(obj) {
            countdown=getCookieValue("secondsremained");
            if (countdown == 0) {
                obj.removeAttr("disabled");
                obj.val("获取验证码");
                return;
            } else {
                obj.attr("disabled", true);
                obj.val("重新发送(" + countdown + ")");
                countdown--;
                editCookie("secondsremained",countdown,countdown+1);
            }
            setTimeout(function() { settime(obj) },1000) //每1000毫秒执行一次
        }
        //校验手机号是否合法
        function isValidMailAddress(){
            var mailAddress= $("#recipient").val();
            var myreg = /^(.*@.*)$/;
            if(!myreg.test(mailAddress)){
                alert('请输入有效的邮箱地址！');
                return false;
            }else{
                return true;
            }
        }
    </script>

</head>
<body>
<ul id="ul_navigator">
    <li class="li_navigator"><a href="../index">首页</a></li>
    <li class="li_navigator"><a href="edit">写信</a></li>
    <li class="li_navigator"><a href="#about">关于</a></li>
</ul>
<div class="pageContainer">
    <div id="main" class="main">
        <div class="mainLeft">
            <h2>致未来的自己</h2>
            <form accept-charset="utf-8" action="/letter/save" class="letterForm" id="new_letter" method="post">
                <table>
                    <tbody>
                    <tr>
                        <th>邮箱：</th>
                        <td><input id="recipient" name="recipient" size="25" type="text" value="E-mail地址"
                                   onclick="clearOnClickEmailInput()">
                        <input type="button" id="sendVCode" value="发送验证码" onclick="sendVCode"></td>
                    </tr>
                    <tr>
                        <th>验证码：</th>
                        <td>
                            <input id="vCode" name="vCode" size="25" type="text">
                        </td>
                    </tr>
                    <tr>
                        <th>主题：</th>
                        <td>
                            <input id="subject" type="text" name="subject" size="40"/>
                        </td>
                    </tr>
                    <tr>
                        <th style="vertical-align: top">内容：</th>
                        <td>
                            <textarea class="emailForm" id="body" name="body" cols="42" rows="20">Hi ,</textarea>
                        </td>
                    </tr>
                    <tr id="exact_date">
                        <th>发信日期：</th>
                        <td>
                            <input type="text" id="date_picker" name="sendDate" size="25"/>
                            <script>
                                laydate({
                                    elem: '#date_picker',
                                    min: laydate.now(+1), //-1代表昨天，-2代表前天，以此类推
                                    max: laydate.now(+36500) //+1代表明天，+2代表后天，以此类推
                                });
                            </script>
                        </td>
                    </tr>

                    <tr>
                        <th>&nbsp;</th>
                        <td><input type="submit" value="发送！" class="bigBlueButton"></td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>
</body>
</html>
