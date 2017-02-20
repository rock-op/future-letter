<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring"
           uri="http://www.springframework.org/tags/form"%>

<%
    Calendar gc = new GregorianCalendar(Locale.CHINA);
    gc.setTime(new Date());
    final String CTX = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
    final String COPY_RIGHT_YEAR = gc.get(Calendar.YEAR) + "";
%>

<c:set var="ctx" value="<%=CTX%>" />
<c:set var="COPY_RIGHT_YEAR" value="<%=COPY_RIGHT_YEAR %>" />
