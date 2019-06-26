<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ attribute name="cstic" type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="csticKey" required="true" type="java.lang.String"%>
<%@ attribute name="pathPrefix" required="false" type="java.lang.String"%>

<c:set var="text" value="${cstic.longText}" />

<c:if test="${not empty text}">
	<div id="${csticKey}.longText" <c:if test="${!cstic.showFullLongText}">style="display:none"</c:if>
		class="cpq-cstic-long-text">${text}</div>
	<form:input type="hidden" id="${csticKey}.showFullLongText" path="${pathPrefix}showFullLongText" />
</c:if>
