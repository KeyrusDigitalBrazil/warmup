<%@ tag language="java" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
		typeSuffix=".checkBox" />
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
</c:if>

<div class="${cssConf:valueStyleClass(cstic)} ${valueStyle}">
	<div class="checkbox cpq-csticValueSelect-single">
		<form:checkbox role="checkbox"
			class="${cssConf:valueStyleClass(cstic)} cpq-csticValueSelect cpq-csticValueSelect-single"
			id="${csticKey}.checkBox"
			path="${pathPrefix}domainvalues[0].selected"
			value="${cstic.domainvalues[0].selected}" 
			required="${required}"
			aria-labelledby="${csticKey}.label ${csticKey}.${value.key}.label ${csticKey}.${value.key}.strikeThroughPrice ${csticKey}.${value.key}.valuePrice"/>
		<config:valuePrice valuePrice="${cstic.domainvalues[0].price}"
			selected="${value.selected}" csticKey="${csticKey}"
			showDeltaPrice="${value.showDeltaPrice}" />
	</div>
	<config:ruleBasedMessages csticKey="${csticKey}" csticValueKey="${value.key}" pathPrefix="${pathPrefix}"
						 messages="${value.messages}"/>
</div>

