<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="overview" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/overview"%>
<%@ taglib prefix="confUtil" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="group" required="true" type="de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup"%>
<%@ attribute name="level" required="true" type="Integer"%>
<%@ attribute name="variantOverview" required="true" type="Boolean"%>

<c:set var="groupcontentstyleclass" value="cpq-overview-group-content" />
<c:if test="${variantOverview}">
	<c:set var="groupcontentstyleclass" value="cpq-variant-overview-group-content" />
</c:if>

<div class="cpq-overview-group-row">
	<div class="${groupcontentstyleclass}">
		<c:if test="${not variantOverview}">
			<div id="${group.groupDescription}_title" class="cpq-overview-group-header" data-level="${level}">
				<c:choose>
					<c:when test="${group.id eq confUtil:generalGroupName()}">
						<spring:message code="sapproductconfig.group.general.title" text="General (Default)" var="groupTitle" />
					</c:when>
					<c:otherwise>
						<c:set var="groupTitle" value="${group.groupDescription}" />
					</c:otherwise>
				</c:choose>
				<div id="cpqGroupTitle" class="cpq-overview-group-title" title="${groupTitle}" data-level="${level}">
					<c:out value="${groupTitle}" />
				</div>
			</div>
		</c:if>		
		<c:if test="${not empty group.characteristicValues}">
			<div class="cpq-group" data-level="${level}">
				<c:forEach var="cstic" items="${group.characteristicValues}" varStatus="csticStatus">
					<c:choose>
						<c:when test="${cstic.valuePositionType eq 'ONLY_VALUE'}">
							<overview:csticOverview cstic="${cstic}"/>
						</c:when>
						<c:otherwise>
							<overview:multiValuedCsticOverview cstic="${cstic}"/>
						</c:otherwise>
					</c:choose>				
				</c:forEach>
			</div>
		</c:if>
	</div>
</div>

<c:if test="${not empty group.subGroups}">
	<c:forEach var="subgroup" items="${group.subGroups}" varStatus="groupStatus">			
		<overview:groupOverview group="${subgroup}" level="${level + 1}" variantOverview ="${variantOverview}"/>
	</c:forEach>
</c:if>
