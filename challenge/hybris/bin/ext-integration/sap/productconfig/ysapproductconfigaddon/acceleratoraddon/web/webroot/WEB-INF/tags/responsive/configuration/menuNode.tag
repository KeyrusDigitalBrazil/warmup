<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="confUtil" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="group" required="true" type="de.hybris.platform.sap.productconfig.facades.UiGroupData"%>
<%@ attribute name="level" required="true" type="java.lang.Integer"%>

<!--  temporary, will be removed once conflicts are displayed on the UI -->
<c:if test="${group.groupType ne 'CONFLICT' and group.groupType ne 'CONFLICT_HEADER'}">

	<c:set var="isMenuNode" value="${confUtil:menuNodeStyleClass(group, level)}" />
	
	<c:choose>
		<c:when test="${group.name eq confUtil:generalGroupName()}">
			<spring:message code="sapproductconfig.group.general.title" text="General (Default)" var="groupTitle" />
		</c:when>
		<c:otherwise>
			<c:set var="groupTitle" value="${group.description}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="mergeWithSubGroup" value="${group.oneConfigurableSubGroup && fn:length(group.subGroups) == 1}" />
	
	<c:if test="${!mergeWithSubGroup}">
		<spring:message code="sapproductconfig.menu.noCfg" text="${groupTitle} is not configurable" var="notConfigurableMessage"
			arguments="${group.description}" />
		<div id="menuNode_${group.id}"
			class="${confUtil:menuNodeStyleClass(group, level)} <c:if test="${group.id eq config.groupIdToDisplay}"> cpq-menu-leaf-selected</c:if>">
			<div id="menuTitle_${group.id}" class="cpq-menu-title"
				<c:if test="${!group.configurable}"> title="${notConfigurableMessage}"</c:if>
				<c:if test="${group.configurable}"> title="${groupTitle}"</c:if>>${groupTitle}</div>
				<c:if test="${!fn:containsIgnoreCase(isMenuNode,'cpq-menu-node cpq-menu-expanded')}">
					<c:set var="groupTooltipKey" value="${confUtil:groupStatusTooltipKey(group)}" />
					<c:choose>
						<c:when test="${!group.configurable}">
							<c:set var="groupTooltip" value="title=\"${notConfigurableMessage}\"" />
						</c:when>
						<c:when test="${groupTooltipKey eq ''}">
							<c:set var="groupTooltip" value="" />
						</c:when>
						<c:otherwise>
							<spring:message code="${groupTooltipKey}" var="groupTooltip" />
							<c:set var="groupTooltip" value="title=\"${groupTooltip}\"" />
						</c:otherwise>
					</c:choose>				
					<div id="cpqMenuStatusIcon" class="cpq-status-icon" ${groupTooltip} >
							<c:if test="${group.numberErrorCstics != 0}">${group.numberErrorCstics}</c:if>
					</div>
				</c:if>
		</div>
	</c:if>
	
	<c:if test="${!group.collapsedInSpecificationTree || mergeWithSubGroup}">
		<c:choose>
			<c:when test="${mergeWithSubGroup}">
				<config:menuNode group="${group.subGroups[0]}" level="${level}" />
			</c:when>
			<c:otherwise>
				<c:set value="${level + 1}" var="subLevel" />
				<c:forEach var="subGroup" items="${group.subGroups}">
					<config:menuNode group="${subGroup}" level="${subLevel}" />
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</c:if>
</c:if>
