<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/responsive/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>


<template:page pageTitle="${pageTitle}">

	<cart:cartValidation/>

	<div>
		<cms:pageSlot position="TopContent" var="feature">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
		 
	   <c:if test="${not empty cartData.entries}">
			   <cms:pageSlot position="CenterLeftContentSlot" var="feature">
				   <cms:component component="${feature}"/>
			   </cms:pageSlot>
		</c:if>
		
		 <c:if test="${not empty cartData.entries}">
			<cms:pageSlot position="CenterRightContentSlot" var="feature">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
			<cms:pageSlot position="BottomContentSlot" var="feature">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</c:if>
				
				
		<c:if test="${empty cartData.entries}">
			<cms:pageSlot position="EmptyCartMiddleContent" var="feature" element="div">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</c:if>
	</div>
</template:page>