<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>

<%@ attribute name="groupType" required="true" type="de.hybris.platform.sap.productconfig.facades.GroupType"%>
<%@ attribute name="key" required="true" type="java.lang.String"%>

<c:if test="${groupType eq 'CONFLICT'}">
	<spring:message code="sapproductconfig.conflict.link.to.configuration" text="View in Configuration" var="viewInCart" />
	<div id="${key}.conflicts" class="cpq-conflict-to-config">
		<a class="cpq-conflict-link-to-config" title="${viewInCart}" href="#">${viewInCart}</a>
	</div>
</c:if>
