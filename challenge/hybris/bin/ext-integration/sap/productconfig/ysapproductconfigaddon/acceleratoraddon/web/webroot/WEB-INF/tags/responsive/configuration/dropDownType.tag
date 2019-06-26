<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="groupType" required="true" type="de.hybris.platform.sap.productconfig.facades.GroupType"%>

<%@ attribute name="csticKey" required="true" type="java.lang.String"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
<%@ attribute name="valueStyle" required="false" type="java.lang.String"%>

<div class="cpq-label-config-link-row">
	<config:label cstic="${cstic}" csticKey="${csticKey}" typeSuffix=".ddlb" />
	<config:conflictLinkToConfig groupType="${groupType}" key="${csticKey}" />
</div>
<config:longText cstic="${cstic}" csticKey="${csticKey}" pathPrefix="${pathPrefix}" />
<config:csticImage cstic="${cstic}" csticKey="${csticKey}" />
<config:csticErrorMessages key="${csticKey}" groupType="${groupType}"
	bindResult="${requestScope['org.springframework.validation.BindingResult.config']}" path="${pathPrefix}value" />
<config:csticErrorMessages key="${csticKey}.additionalValue" groupType="${groupType}"
	bindResult="${requestScope['org.springframework.validation.BindingResult.config']}" path="${pathPrefix}additionalValue" />
<config:ruleBasedMessages csticKey="${csticKey}" pathPrefix="${pathPrefix}" messages="${cstic.messages}" />

<c:if test="${cstic.required}">
	<c:set var="required" value="required" />
</c:if>

<div class="${cssConf:valueStyleClass(cstic)} ${valueStyle}">
	<form:select id="${csticKey}.ddlb" class="form-control cpq-ddlb " path="${pathPrefix}value" required="${required}">
		<c:if test="${empty cstic.value}">
			<form:option value="NULL_VALUE" class="${cssConf:valueStyleClass(cstic)}">
				<spring:message code="sapproductconfig.ddlb.select.text" text="Please select" />
			</form:option>
		</c:if>
		<c:forEach var="value" items="${cstic.domainvalues}" varStatus="status">
			<c:set var="optionText" value="${value.langdepname}" />
			<c:set var="formattedPrice" value="" />
			<c:if test="${value.selected}">
				<c:set var="selectedValue" value="${value}" />
				<c:set var="selectedValueIndex" value="${status.index}" />
				<c:set var="selectedValueKey" value="${csticKey}.${value.key}.option" />
			</c:if>
			<c:if test="${value.price.formattedValue ne '-' && (value.price.value.unscaledValue() != 0 || value.selected)}">
				<c:choose>
					<c:when test="${value.selected && value.showDeltaPrice}">
						<spring:message code="sapproductconfig.deltaprcices.selected" text="Selected" var="formattedPrice" />
					</c:when>
					<c:otherwise>
						<c:if test="${value.price.value.unscaledValue() != 0}">
							<c:choose>
								<c:when test="${fn:contains(value.price.formattedValue, '-')}">
									<c:set value="${value.price.formattedValue}" var="formattedPrice" />
								</c:when>
								<c:otherwise>
									<c:set value="+${value.price.formattedValue}" var="formattedPrice" />
								</c:otherwise>
							</c:choose>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:if>
			<form:option id="${csticKey}.${value.key}.option" value="${value.name}" label="${optionText}"
				aria-labelledby="${csticKey}.label ${csticKey}.${value.key}.option" data-price="${formattedPrice}"
				data-optionkey="${selectedValueKey}" data-promostyle="${cssConf:valuePromoStyleClass(cstic, value)}" data-isdeltaprice="${value.showDeltaPrice}" />
		</c:forEach>
	</form:select>
</div>

<config:ruleBasedMessages csticKey="${csticKey}" csticValueKey="${selectedValue.key}"
	pathPrefix="${pathPrefix}domainvalues[${selectedValueIndex}]." messages="${selectedValue.messages}" />

<c:if test="${cstic.type == 'DROPDOWN_ADDITIONAL_INPUT'}">
	<config:additionalValue cstic="${cstic}" csticKey="${csticKey}" pathPrefix="${pathPrefix}" />
</c:if>
