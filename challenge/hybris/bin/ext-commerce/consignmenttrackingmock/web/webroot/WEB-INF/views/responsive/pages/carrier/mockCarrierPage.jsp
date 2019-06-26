<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Mock Carrier</title>
<script type="text/javascript"
	src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
</head>
<body>
	<c:set var="contextPath" value="${fn:split(pageContext.request.contextPath, '/')[0]}" />
	<img alt="logo" src="/${contextPath}/_ui/responsive/common/images/logo-hybris-responsive.png" />
	<h2>Welcome to Carrier Website!</h2>
	<h5>(This is mock Home page)</h5>
</body>
</html>