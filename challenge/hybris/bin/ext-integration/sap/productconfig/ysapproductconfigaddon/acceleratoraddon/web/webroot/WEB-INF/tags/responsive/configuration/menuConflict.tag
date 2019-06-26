<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="configData" required="true" type="de.hybris.platform.sap.productconfig.facades.ConfigurationData"%>

<spring:message code="sapproductconfig.group.conflict.header" text="Resolve issues (static)" var="conflictMenuTitle"/>
<spring:message code="sapproductconfig.group.conflict.tooltip" text="This section contains conflicts" var="conflictMenuTooltip"/>

<c:set var="conflictHeader" value="${configData.groups[0]}" />

<c:if test="${fn:containsIgnoreCase(conflictHeader.id,'CONFLICT_HEADER')}">
	<div id="menuNode_${conflictHeader.id}" class="${cssConf:menuConflictStyleClass(conflictHeader)} cpq-menu-conflict-number"> 
		<div id="conflictMenuTitle" class="cpq-menu-conflict-title">${conflictMenuTitle}</div>
		<div id="cpqMenuStatusIcon" class="cpq-status-icon" title="${conflictMenuTooltip}">
			<c:if test="${conflictHeader.numberErrorCstics != 0}">${conflictHeader.numberErrorCstics}</c:if>
		</div>
	</div>
	<c:if test="${!conflictHeader.collapsedInSpecificationTree}">
		<c:forEach var="conflict" items="${conflictHeader.subGroups}">
			<config:menuConflictNode conflict="${conflict}" config="${configData}"/>
		</c:forEach>
	</c:if>
</c:if>			
		
