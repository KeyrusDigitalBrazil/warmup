<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="back-link">
	<button type="button" class="invoiceTopBackBtn" data-back-to-invoicelist="${backLink}">
		<span class="glyphicon glyphicon-chevron-left"></span>
	</button>
    <span class="label"><spring:theme code="text.accountsummary.invoice.title.details" text="Invoice Details" /></span>
</div>
