<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="/my-account/order/{/orderCode}/review/{/consignmentcode}" var="orderReviewUrl" htmlEscape="false">
	<spring:param name="orderCode" value="${orderCode}"/>
	<spring:param name="consignmentcode" value="${consignment.code}"/>
</spring:url>
<div class="label-order" style="padding-top: 10px;">
	<c:choose>
		<c:when test="${consignment.reviewable}">
			<a id="order-review-link" href="${fn:escapeXml(orderReviewUrl)}" class="btn btn-default btn-block">
				<spring:theme code="text.account.order.review" text="Review"/>
			</a>
		</c:when>
		<c:otherwise>
			<button id="order-review-link" disabled="disabled" class="btn btn-default btn-block">
				<spring:theme code="text.account.order.reviewed" text="Reviewed"/>
			</button>
		</c:otherwise>
	</c:choose>
</div>
