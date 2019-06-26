<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="groupType" required="true" type="de.hybris.platform.sap.productconfig.facades.GroupType"%>

<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
<%@ attribute name="csticNo" required="true" type="java.lang.Integer" %>
<%@ attribute name="numCstic" required="true" type="java.lang.Integer" %>
<%@ attribute name="conflictGrpNo" required="false" type="java.lang.Integer" %>

<c:set var="valueStyle" value=" col-xs-12" />

<c:set var="csticKey" value="${cstic.key}" />
<c:if test="${groupType eq 'CONFLICT'}">
	<c:set var="conflictGrpNo" value="${(empty conflictGrpNo) ? '1' : conflictGrpNo}"/>
	<c:set var="csticKey" value="conflict.${conflictGrpNo}.${csticKey}"/>
</c:if>
		
<c:if test="${cstic.visible}">	
	   <config:conflictSuggestion cstic="${cstic}" groupType="${groupType}"	suggNo="${csticNo}" noOfsugg="${numCstic}" />
	<div class="cpq-cstic<c:if test="${groupType eq 'CONFLICT'}"> cpq-conflictCstic</c:if>">
		<c:choose>
			<c:when test="${cstic.type == 'STRING'}">
				<config:stringType pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:when test="${cstic.type == 'DROPDOWN'  || cstic.type == 'DROPDOWN_ADDITIONAL_INPUT'}">
				<config:dropDownType pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:when test="${cstic.type == 'RADIO_BUTTON' || cstic.type == 'RADIO_BUTTON_ADDITIONAL_INPUT'}">
				<config:radioButtonType pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:when test="${cstic.type == 'CHECK_BOX'}">
				<config:checkBoxType pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:when test="${cstic.type == 'CHECK_BOX_LIST'}">
				<config:checkBoxListType pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:when test="${cstic.type == 'NUMERIC'}">
				<config:numericType pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:when test="${cstic.type == 'READ_ONLY'}">
				<config:readOnlyType pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:when test="${cstic.type == 'SINGLE_SELECTION_IMAGE' || cstic.type == 'READ_ONLY_SINGLE_SELECTION_IMAGE'}">
				<config:csticImageSelection pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:when test="${cstic.type == 'MULTI_SELECTION_IMAGE' || cstic.type == 'READ_ONLY_MULTI_SELECTION_IMAGE'}">
				<config:csticImageSelection pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:when>
			<c:otherwise>
				<config:notImplementedType pathPrefix="${pathPrefix}" cstic="${cstic}" csticKey="${csticKey}" valueStyle="${valueStyle}" groupType="${groupType}" />
			</c:otherwise>
		</c:choose>
		<form:input type="hidden" id="${csticKey}.key" path="${pathPrefix}key" />

	</div>
</c:if>
