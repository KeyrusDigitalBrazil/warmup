<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>



<spring:url value="/my-account/order/${overviewUiData.sourceDocumentId}" var="orderUrl" />


<button type="submit" id="cpqBackToOrderBtn" class="cpq-btn-backToOrder" data-back-to-url="${orderUrl}">
	<spring:message code="sapproductconfig.overview.backtoorder" text="Back To Order" />
</button>
