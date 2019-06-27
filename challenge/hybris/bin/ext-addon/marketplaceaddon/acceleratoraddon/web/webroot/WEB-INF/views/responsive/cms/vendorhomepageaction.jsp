<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<spring:htmlEscape defaultHtmlEscape="true" />
<span class="well-headline-sub"> 
	<spring:theme code="text.consignment.fulfilled.by"/> 
	<spring:url value='/v/{/consignmentvendorcode}' var="vendorUrl" htmlEscape="false">
		<spring:param name="consignmentvendorcode" value="${consignment.vendor.code}"/>
	</spring:url>
	<a class="link-vendor" href="${fn:escapeXml(vendorUrl)}">${fn:escapeXml(consignment.vendor.name)}</a>
</span>
