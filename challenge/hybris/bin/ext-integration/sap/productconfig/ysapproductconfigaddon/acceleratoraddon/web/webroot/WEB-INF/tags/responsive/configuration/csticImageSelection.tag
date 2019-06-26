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
	<c:if test="${cstic.type == 'MULTI_SELECTION_IMAGE' || cstic.type == 'READ_ONLY_MULTI_SELECTION_IMAGE'}">
		<c:set var="suffix" value=".checkboxGroupWithImage" />
	</c:if>
	<c:if test="${cstic.type == 'SINGLE_SELECTION_IMAGE' || cstic.type == 'READ_ONLY_SINGLE_SELECTION_IMAGE'}">
		<c:set var="suffix" value=".radioGroupWithImage" />
	</c:if>
	<config:label cstic="${cstic}" csticKey="${csticKey}" typeSuffix="${suffix}" />
	<config:conflictLinkToConfig groupType="${groupType}" key="${csticKey}" />
</div>
<config:longText cstic="${cstic}" csticKey="${csticKey}" pathPrefix="${pathPrefix}" />
<config:csticImage cstic="${cstic}" csticKey="${csticKey}" />
<config:csticErrorMessages key="${csticKey}" groupType="${groupType}"
	bindResult="${requestScope['org.springframework.validation.BindingResult.config']}" path="${pathPrefix}value" />
<config:csticErrorMessages key="${csticKey}.additionalValue" groupType="${groupType}"
	bindResult="${requestScope['org.springframework.validation.BindingResult.config']}" path="${pathPrefix}additionalValue"/>
<config:ruleBasedMessages csticKey="${csticKey}" pathPrefix="${pathPrefix}" messages="${cstic.messages}"/>

<div id="${csticKey}${suffix}" class="${cssConf:valueStyleClass(cstic)} ${valueStyle}">
	<c:forEach var="value" items="${cstic.domainvalues}" varStatus="status">
		<c:if test="${cstic.type == 'MULTI_SELECTION_IMAGE' || cstic.type == 'SINGLE_SELECTION_IMAGE' || value.selected}">
			<c:set var="clickHandlerStyle" value="" />
			<c:set var="nameAttrValue" value="" />
				
			<c:if test="${cstic.type == 'MULTI_SELECTION_IMAGE'}">
				<c:set var="clickHandlerStyle" value="cpq-cstic-value-container-multi" />
				<form:input id="${csticKey}.${value.key}.checkBoxWithImage" class="hidden"
	                        disabled="${value.readonly}" path="${pathPrefix}domainvalues[${status.index}].selected" value="${value.selected}" />
				<c:set var="nameAttrValue" value="${pathPrefix}domainvalues[${status.index}].selected" />
			</c:if>
			
			<c:if test="${cstic.type == 'SINGLE_SELECTION_IMAGE'}">
				<c:set var="clickHandlerStyle" value="cpq-cstic-value-container-single" />
				<c:set var="nameAttrValue" value="${pathPrefix}value" />
			</c:if>
			
			<c:if test="${cstic.type == 'SINGLE_SELECTION_IMAGE' || cstic.type == 'READ_ONLY_SINGLE_SELECTION_IMAGE'}">
				<div id="${csticKey}.${value.key}.valueName" class="hidden">${value.name}</div>
			</c:if>
			
			<div id="${csticKey}.${value.key}.container" class="cpq-cstic-value-with-image ${clickHandlerStyle} <c:if test="${cstic.type == 'SINGLE_SELECTION_IMAGE' || cstic.type == 'MULTI_SELECTION_IMAGE'}"> cpq-cstic-value-with-image-changeable</c:if>">
				<div id="${csticKey}.${value.key}.imgContainer" tabindex="0" name="${nameAttrValue}" 
					class="cpq-cstic-value-image-container  <c:if test="${value.selected}">cpq-cstic-image-value-selected" checked="checked"</c:if>">
					<c:forEach var="medium" items="${value.media}">
						<c:if test="${medium.format eq 'VALUE_IMAGE'}">
							<img id="${csticKey}.${value.key}.img" class="cpq-cstic-value-image" alt="${cstic.langdepname}"
								src="${medium.url}" aria-labelledby="${csticKey}.label ${csticKey}.${value.key}.label ${csticKey}.${value.key}.strikeThroughPrice ${csticKey}.${value.key}.valuePrice"/>
						</c:if>
					</c:forEach>
				</div>
				<label id="${csticKey}.${value.key}.label" class="cpq-csticValueImageLabel ${cssLabelClass}"
					for="${csticKey}.${value.key}.container">${value.langdepname}
					<c:if test="${cstic.type == 'SINGLE_SELECTION_IMAGE'}">
						<config:csticValueAnalytics csticValueKey="${csticKey}.${value.key}" pathPrefix="${pathPrefix}"/>
					</c:if>
				</label>
				<config:valuePrice valuePrice="${value.price}" selected="${value.selected}" csticKey="${csticKey}.${value.key}" showDeltaPrice="${value.showDeltaPrice}" />
			</div>
		</c:if>
	</c:forEach>
	
	<c:if test="${cstic.type == 'SINGLE_SELECTION_IMAGE'}">
   	<form:input id="${csticKey}.radioButtonWithImage" class="hidden" value="${cstic.value}" path="${pathPrefix}value" />
   </c:if>

</div>
