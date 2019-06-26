<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-account/my-quote/" var="orderQuoteLink" />

<c:set var="searchUrl" value="/my-account/my-quotes?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />

<div class="account-section-header">
	<spring:theme code="text.account.quotes.myquotes" />
</div>

<div class="account-section-content	col-md-6 col-md-push-3 content-empty">
	<ycommerce:testId code="orderHistory_noOrders_label">
		<spring:theme code="sap.account.function.not.supported" />
	</ycommerce:testId>
</div>

