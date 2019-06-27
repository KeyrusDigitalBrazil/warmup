<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>



<spring:url value="/my-account/saved-carts/${overviewUiData.sourceDocumentId}" var="cartUrl" />


<button type="submit" id="cpqBackToSavedCartBtn" class="cpq-btn-backToOrder" data-back-to-url="${cartUrl}">
	<spring:message code="sapproductconfig.overview.backtosavedcart" text="Back To Saved Cart" />
</button>
