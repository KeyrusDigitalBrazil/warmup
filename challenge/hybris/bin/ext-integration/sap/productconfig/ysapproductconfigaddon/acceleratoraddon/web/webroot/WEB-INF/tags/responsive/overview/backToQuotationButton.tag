<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>



<spring:url value="/my-account/my-quotes/${overviewUiData.sourceDocumentId}" var="quoteUrl" />


<button type="submit" id="cpqBackToQuotationBtn" class="cpq-btn-backToQuotation" data-back-to-url="${quoteUrl}">
	<spring:message code="sapproductconfig.overview.backtoquotation" text="Back To Quotation" />
</button>
