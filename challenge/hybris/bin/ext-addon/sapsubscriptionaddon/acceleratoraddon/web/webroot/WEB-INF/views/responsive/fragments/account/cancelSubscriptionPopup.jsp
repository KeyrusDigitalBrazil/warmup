<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:url value="/my-account/subscription/${subscriptionId}/cancel"
	var="cancelSubscriptionUrl" />

<form:form id="subscriptionCancellationForm"
	name="subscriptionCancellationForm"
	action="${fn:escapeXml(cancelSubscriptionUrl)}" method="post"
	commandName="subscriptionCancellationForm">

	<div class="mini-cart js-mini-cart">

		<div class="mini-cart-body">

			<div class="mini-cart-totals">
				<div class="key">
					<spring:theme code="text.account.cancelsubscription.effDate"/>
					
				</div>
				<div class="value">
					<fmt:formatDate value="${subscriptionData.endDate}" dateStyle="long" timeStyle="short" type="date"/>
				</div>
			</div>

			<input type="hidden" name="subscriptionEndDate"
				value="${subscriptionData.validTillDate}" /> <input type="hidden"
				name="version" value="${fn:escapeXml(version)}"/> 

			<button id="cancelsub" type="submit"
				class="btn btn-default btn-block">
				<spring:theme code="text.account.cancelsubscription.proceed" />
			</button>
			
			<a href="" class="btn btn-default btn-block js-mini-cart-close-button">
				<spring:theme code="text.account.cancelsubscription.cancel"/>
			</a>
		</div>

	</div>


</form:form>
