<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="addon-b2b-order" tagdir="/WEB-INF/tags/addons/ysapordermgmtb2baddon/responsive/order" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="searchUrl" value="/my-account/orders?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>

<div class="account-section-header">
	<spring:theme code="text.account.orderHistory" />
</div>

<addon-b2b-order:orderListing searchUrl="${searchUrl}" messageKey="text.account.orderHistory.page"/>