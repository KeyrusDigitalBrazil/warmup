<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="variantOverview" value="${overviewUiData.overviewMode eq 'VARIANT_OVERVIEW'}"/>
<c:set var="variantOverviewFromCartBound" value="${overviewUiData.overviewMode eq 'VARIANT_OVERVIEW_FROM_CART_BOUND_CONFIG'}"/>
<c:choose>
	<c:when test="${variantOverview}">
		<spring:url value="/${product.baseProduct}/configuratorPage/CPQCONFIGURATOR" var="configUrl" />
	</c:when>
	<c:when test="${variantOverviewFromCartBound}">
		<spring:url value="/cart/${overviewUiData.cartEntryNumber}/configureOnDraft/CPQCONFIGURATOR" var="configUrl" />
	</c:when>
	<c:otherwise>
		<spring:url value="/cart/${overviewUiData.cartEntryNumber}/configuration/CPQCONFIGURATOR" var="configUrl" />
	</c:otherwise>
</c:choose>


<button type="submit" id="cpqBackToConfigBtn" class="cpq-btn-backToConfig" data-back-to-url="${configUrl}">
	<spring:message code="sapproductconfig.overview.backtoconfiguration" text="Back To Configuration" />
</button>



