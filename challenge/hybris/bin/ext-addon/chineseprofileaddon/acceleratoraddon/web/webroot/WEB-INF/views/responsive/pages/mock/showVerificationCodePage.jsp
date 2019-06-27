<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Verification Code</title>

</head>
<body style="font-family: arial;">
	<c:set var="hybrisLogoImgUrl" value="${contextPath}/_ui/addons/chineseprofileaddon/responsive/common/images/mock/logo-hybris-responsive.png"/>
	<img alt="logo"
		src="${fn:escapeXml(hybrisLogoImgUrl)}" />

<br/><br/><br/><br/><br/><br/>
<c:if test="${not empty verificationData.mobileNumber}">
	Mobile Number : <span id="mobileNumber">${fn:escapeXml(verificationData.mobileNumber)}</span><br/>
</c:if>
Verification Code : <span id="verificationCode">${fn:escapeXml(verificationData.verificationCode)}</span>
</body>