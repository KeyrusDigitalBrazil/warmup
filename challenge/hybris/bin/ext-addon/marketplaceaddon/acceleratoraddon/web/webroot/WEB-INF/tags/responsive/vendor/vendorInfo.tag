<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="vendor" required="true" type="de.hybris.platform.marketplacefacades.vendor.data.VendorData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="${vendor.url}" var="vendorUrl" htmlEscape="false"/>

<c:if test="${not empty vendor}">
	<div>
		<span class="sold-by">
			<spring:theme code="text.store.seller" text="Sold by " htmlEscape="false"/>
		</span>
		<span class="text-uppercase">
			<a href="${fn:escapeXml(vendorUrl)}">${fn:escapeXml(vendor.name)}</a>
		</span>
	</div>
</c:if>

