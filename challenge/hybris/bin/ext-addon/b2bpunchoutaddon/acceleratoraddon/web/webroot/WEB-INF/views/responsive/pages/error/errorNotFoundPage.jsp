<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
	
	<c:url value="/" var="homePageUrl" />


	<cms:pageSlot position="MiddleContent" var="comp" >
		<cms:component component="${comp}"/>
	</cms:pageSlot>
	<cms:pageSlot position="BottomContent" var="comp" element="div" class="errorNotFoundPageBottom">
		<cms:component component="${comp}"/>
	</cms:pageSlot>
	<cms:pageSlot position="SideContent" var="feature" element="div" class="errorNotFoundPageSide">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	
	<div class="error-page">
		<a class="btn btn-default js-shopping-button" href="${fn:escapeXml(homePageUrl)}">
			<spring:theme text="Continue Shopping" code="general.continue.shopping"/>
		</a>
	</div>

</template:page>