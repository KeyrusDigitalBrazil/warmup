<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/accountsummaryaddon/responsive/company" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="account-section-header no-border">
	<spring:theme code="text.company.unittree.accountsummary.label"/>
</div>


<div id="accordion" class="panel-group accordion">
    <company:unitTree node="${rootNode}"/>
</div>