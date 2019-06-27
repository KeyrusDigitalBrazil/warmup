<%@ tag language="java" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="cssConf"
	uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="cstic" required="true"
	type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="groupType" required="true"
	type="de.hybris.platform.sap.productconfig.facades.GroupType"%>

<%@ attribute name="csticKey" required="true" type="java.lang.String"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
<%@ attribute name="valueStyle" required="false" type="java.lang.String"%>

<div class="cpq-label-config-link-row">
	<config:label cstic="${cstic}" csticKey="${csticKey}"
		typeSuffix=".input" />
	<config:conflictLinkToConfig groupType="${groupType}" key="${csticKey}" />
</div>
<config:longText cstic="${cstic}" csticKey="${csticKey}"
	pathPrefix="${pathPrefix}" />
<config:csticImage cstic="${cstic}" csticKey="${csticKey}" />
<config:csticErrorMessages key="${csticKey}" groupType="${groupType}"
	bindResult="${requestScope['org.springframework.validation.BindingResult.config']}"
	path="${pathPrefix}value" />
<config:ruleBasedMessages csticKey="${csticKey}" pathPrefix="${pathPrefix}" messages="${cstic.messages}"/>

<c:if test="${cstic.required}">
	<c:set var="required" value="required" />
	<c:set var="ariaRequired" value="true" />
</c:if>

<div class="${cssConf:valueStyleClass(cstic)} ${valueStyle}">
	<c:choose>
		<c:when test="${cstic.maxlength > 0}">
			<form:input id="${csticKey}.input" class="form-control" type="text"
				path="${pathPrefix}value" maxlength="${cstic.maxlength}"
				required="${required}" aria-required="${ariaRequired}" />
		</c:when>
		<c:otherwise>
			<form:input id="${csticKey}.input" class="form-control" type="text"
				path="${pathPrefix}value" required="${required}"
				aria-required="${ariaRequired}" />
		</c:otherwise>
	</c:choose>
</div>
