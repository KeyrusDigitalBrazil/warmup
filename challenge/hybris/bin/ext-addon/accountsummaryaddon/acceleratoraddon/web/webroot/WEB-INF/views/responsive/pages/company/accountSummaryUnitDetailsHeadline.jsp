<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/accountsummary-unit" var="accountSummaryUnitUrl" htmlEscape="false"/>
<div class="back-link">
    <a href="${fn:escapeXml(accountSummaryUnitUrl)}"><span class="glyphicon glyphicon-chevron-left"></span></a>
    <span class="label"><spring:theme code="text.company.accountsummary.details"/></span>
</div>
