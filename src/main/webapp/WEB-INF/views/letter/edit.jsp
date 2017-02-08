<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<html>
<head>
    <title>futureMe.xin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/main.css"/>
    <script type="text/javascript" src="http://www.js-css.cn/jscode/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/laydate/laydate.js"></script>
    <script>
        window.onload = function () {
            var subjectElement = document.getElementById("subject");
            var date = new Date();
            subjectElement.value = "一封来自" + date.getFullYear() + "年" + date.getMonth() + "月" + date.getDay() + "日的信";
        }

        function clearOnClickEmailInput() {
            var recipientElement = document.getElementById("recipient");
            if (recipientElement.value == "E-mail地址") {
                recipientElement.value = "";
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
            <h2>写封信给未来的自己</h2>
            <form accept-charset="utf-8" action="/letter/save" class="letterForm" id="new_letter" method="post">
                <table>
                    <tbody>
                    <tr>
                        <th>邮箱：</th>
                        <td><input id="recipient" name="recipient" size="45" type="text" value="E-mail地址"
                                   onclick="clearOnClickEmailInput()"></td>
                    </tr>
                    <tr>
                        <th>主题：</th>
                        <td>
                            <input id="subject" type="text" name="subject" size="45"/>
                        </td>
                    </tr>
                    <tr>
                        <th style="vertical-align: top">内容：</th>
                        <td>
                            <textarea class="emailForm" id="body" name="body" cols="45" rows="20">Hi ,</textarea>
                        </td>
                    </tr>
                    <tr id="exact_date">
                        <th>发信日期：</th>
                        <td>
                            <input type="text" id="date_picker" name="sendDate"/>
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
                        <th>隐私类型</th>
                        <td>
                            <input checked="checked" class="form" id="letterPrivate" name="privacyType"
                                   style="border-style: none" type="radio" value="false">&nbsp;私密<br>
                            <input class="form" id="letterPublic" name="privacyType" style="border-style: none"
                                   type="radio" value="true">&nbsp;公开（匿名）
                        </td>
                    </tr>
                    <%--<tr>--%>
                        <%--<th>邮箱校验码</th>--%>
                        <%--<td>--%>
                            <%--<input id="verification" name="verification" size="25" type="text">--%>
                            <%--<button id="send_vcode" name="send_vcode" onclick="">获取校验码</button>--%>
                        <%--</td>--%>
                    <%--</tr>--%>
                    <tr>
                        <th>&nbsp;</th>
                        <td><input type="submit" value="发送！" class="bigBlueButton">
                            <br>&nbsp;
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>
</body>
</html>
