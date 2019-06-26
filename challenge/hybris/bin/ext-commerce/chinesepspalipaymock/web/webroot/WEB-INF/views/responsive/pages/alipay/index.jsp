<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="contextPath" value="${fn:split(pageContext.request.contextPath, '/')[0]}" />
<title>Welcome to chinesepspalipay mock</title>
<script type="text/javascript" src="/${contextPath}/_ui/shared/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="/${contextPath}/_ui/shared/js/chinesepspalipaymock.js"></script>

</head>
<body style="font-family: arial;">

	<img alt="logo" src="/${contextPath}/_ui/shared/images/mock/logo-hybris-responsive.png" />

	<h2>Welcome to "chinesepspalipaymock" extension</h2>
	<h5>(This is mock Home page)</h5>
	</body>
</html>
