<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<spring:htmlEscape defaultHtmlEscape="true"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	
	<div class="row">
		<div class="checkout-headline">
			<c:if test="${paymentStatus == 'true'}" >
				<p><spring:theme code="order.paymentStatus.successful" /></p>
			</c:if>
			<c:if test="${paymentStatus == 'false'}" >
				<p><spring:theme code="order.paymentStatus.failed" /></p>
			</c:if>
			<c:if test="${paymentStatus == 'notFound'}" >
				<p><spring:theme code="order.paymentStatus.notFound" /></p>
			</c:if>
		</div>
	
		<div class="col-sm-5 col-md-4 col-lg-4">
			<div class="step-body hop-payment-border">
				<div class="checkout-shipping hop-payment-border">
					<div class="form-group">
						<label class="control-label "> 
							<spring:theme code="order.paymentReturn.closeCountDown" /><span id="countdown"></span>
						</label>
					</div>
				</div>
			</div>
		</div>
	    
		<div class="col-sm-12 col-lg-12">
			<br class="hidden-lg">
			<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>
	
</template:page>
