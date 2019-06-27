<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="groupType" required="true" type="de.hybris.platform.sap.productconfig.facades.GroupType"%>

<%@ attribute name="csticKey" required="true" type="java.lang.String"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
<%@ attribute name="valueStyle" required="false" type="java.lang.String"%>

<div class="cpq-label-config-link-row">
	<config:label cstic="${cstic}" csticKey="${csticKey}" typeSuffix=".checkBoxList" />
	<config:conflictLinkToConfig groupType="${groupType}" key="${csticKey}" />
</div>
<config:longText cstic="${cstic}" csticKey="${csticKey}" pathPrefix="${pathPrefix}" />
<config:csticImage cstic="${cstic}" csticKey="${csticKey}" />
<config:csticErrorMessages key="${csticKey}" groupType="${groupType}"
	bindResult="${requestScope['org.springframework.validation.BindingResult.config']}" path="${pathPrefix}value" />
<config:ruleBasedMessages csticKey="${csticKey}" pathPrefix="${pathPrefix}" messages="${cstic.messages}" />

<div id="${csticKey}.checkBoxList" class="${cssConf:valueStyleClass(cstic)} ${valueStyle}">
	<c:forEach var="value" items="${cstic.domainvalues}" varStatus="status">
		<c:choose>
			<c:when test="${status.index == 0}">
				<c:set value="cpq-csticValueSelect-first" var="cssValueClass" />
				<c:set value="cpq-csticValueLabel-first" var="cssLabelClass" />
			</c:when>
			<c:otherwise>
				<c:set value="" var="cssValueClass" />
				<c:set value="" var="cssLabelClass" />
			</c:otherwise>
		</c:choose>
		<div class="checkbox">
			<div class="cpq-selectLine">
				<form:checkbox id="${csticKey}.${value.key}.checkBox" class="capitalize cpq-csticValueSelect ${cssValueClass}"
					disabled="${value.readonly}" path="${pathPrefix}domainvalues[${status.index}].selected" value="${value.selected}"
					aria-labelledby="${csticKey}.label ${csticKey}.${value.key}.label ${csticKey}.${value.key}.strikeThroughPrice ${csticKey}.${value.key}.valuePrice" />
				<label id="${csticKey}.${value.key}.label" for="${csticKey}.${value.key}.checkBox"
					class="cpq-csticValueLabel ${cssLabelClass}">${value.langdepname}</label>
				<config:valuePrice valuePrice="${value.price}" selected="${value.selected}" csticKey="${csticKey}.${value.key}"
					showDeltaPrice="${value.showDeltaPrice}" />
			</div>
			<config:ruleBasedMessages csticKey="${csticKey}" csticValueKey="${value.key}"
				pathPrefix="${pathPrefix}domainvalues[${status.index}]." messages="${value.messages}" />
		</div>
	</c:forEach>
</div>

