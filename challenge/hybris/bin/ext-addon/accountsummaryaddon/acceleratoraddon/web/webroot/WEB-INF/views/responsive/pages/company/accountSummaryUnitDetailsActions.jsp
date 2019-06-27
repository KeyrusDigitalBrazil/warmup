<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/accountsummary-unit" var="accountSummaryUnitUrl" htmlEscape="false"/>
<div class="row">
	<div class="col-xs-12 col-sm-6 col-md-4 col-lg-3">
		<div class="accountActions-bottom">
			<button type="button" class="form btn-default btn btn-block accountSummaryUnitBackBtn" data-back-to-account-summary="${fn:escapeXml(accountSummaryUnitUrl)}">
				<spring:theme code="text.company.accountsummary.unit.details.backToAccountSummary"/>
			</button>
		</div>
	</div>
</div>