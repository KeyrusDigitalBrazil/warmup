<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<template:page pageTitle="${pageTitle}">

	<cms:pageSlot position="Section1" var="feature" element="div" class="product-grid-section1-slot">
		<cms:component component="${feature}" element="div" class="yComponentWrapper map product-grid-section1-component"/>
	</cms:pageSlot>
	
	<div class="vendor-index-title"><spring:theme code="text.vendors.page.title" text="Our Vendors"/></div>

	<div class="row">		
		<div class="col-sm-12 col-md-12">
			<cms:pageSlot position="VendorGridSlot" var="feature" element="div" class="yComponentWrapper">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>
</template:page>