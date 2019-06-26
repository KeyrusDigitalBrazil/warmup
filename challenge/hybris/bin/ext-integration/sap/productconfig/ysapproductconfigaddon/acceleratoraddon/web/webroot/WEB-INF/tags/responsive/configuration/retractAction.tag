<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ attribute name="groupType" required="true" type="de.hybris.platform.sap.productconfig.facades.GroupType"%>
<%@ attribute name="key" required="true" type="java.lang.String"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<c:if test="${groupType eq 'CONFLICT'}">
	<spring:message code="sapproductconfig.conflict.retractValue" text="Retract Value" var="retractText" />
	<div id="${key}.conflicts" class="cpq-conflict-retractValue">
		<a href="" class="cpq-conflict-retractValue-button" title="${retractText}" >${retractText}</a>
	</div>
	<form:input type="hidden" id="${key}.retractValue" path="${pathPrefix}retractTriggered" />
</c:if>
