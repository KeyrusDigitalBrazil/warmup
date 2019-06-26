<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<spring:url value="/checkout/multi/summary/payRightNow/{/code}" var="payRightNowUrl" htmlEscape="false">
	<spring:param name="code" value="${orderData.code}" />
</spring:url>

<c:if test="${orderData.status.code != 'CANCELLED' and orderData.paymentStatus.code eq 'NOTPAID'}">
	<div class="label-order">
		<a id="payRightNowButton" class="payment-action" data-href="${fn:escapeXml(payRightNowUrl)}"
				data-payment="${fn:escapeXml(orderData.chinesePaymentInfo.paymentProvider)}" href="javascript:;" target="_blank"> 
			<spring:theme code="order.detail.button.pay.immediately" text="PayImmediately" />
		</a>
	</div>
</c:if>
