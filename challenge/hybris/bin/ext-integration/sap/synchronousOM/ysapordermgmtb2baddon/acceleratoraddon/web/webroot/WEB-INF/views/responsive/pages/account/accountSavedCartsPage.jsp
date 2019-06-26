<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<spring:url value="/my-account/saved-carts/" var="savedCartsLink" htmlEscape="false"/>
<c:set var="searchUrl" value="/my-account/saved-carts?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>

<div class="account-section-header">
    <spring:theme code="text.account.savedCarts"/>
</div>

<div class="account-section-content	col-md-6 col-md-push-3 content-empty">
	<ycommerce:testId code="orderHistory_noOrders_label">
		<spring:theme code="sap.account.function.not.supported" />
	</ycommerce:testId>
</div>
