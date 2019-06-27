<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="${redirectUrl}" var="continueUrl" htmlEscape="false"/>
<template:page pageTitle="${pageTitle}">

	<div class="row">
		<div class="checkout-headline">

			<c:choose>
				<c:when test="${isAnonymousUser}">
					<p>
						<spring:theme code="order.payment.anonymous.failed" />
					</p>
				</c:when>
				<c:otherwise>
					<p>
						<spring:theme code="order.payment.failed" />
					</p>
				</c:otherwise>
			</c:choose>
			
		</div>


		<div class="col-sm-12 col-lg-12">
			<br class="hidden-lg">
			<cms:pageSlot position="SideContent" var="feature" element="div"
				class="checkout-help">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</div>

</template:page>
