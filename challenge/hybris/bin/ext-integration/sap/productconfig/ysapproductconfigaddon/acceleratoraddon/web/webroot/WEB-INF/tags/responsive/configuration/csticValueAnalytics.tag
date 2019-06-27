<%@ tag language="java" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="config"
	tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>

<%@ attribute name="csticValueKey" required="true" type="java.lang.String"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<c:set var="popularityTooltip" value="" />
<c:set var="popularityInPercent" value="" />

<spring:theme code="sapproductconfig.analytics.popularityInPercent"
			text="{0}%" arguments="XX" var="popularityInPercent" />
<spring:theme code="sapproductconfig.analytics.popularityInPercent.tooltip"
			text="{0}% of customers chose this option" arguments="XX" var="popularityTooltip" />

<%-- Never display analytics data on sub item level --%>
<c:if
	test="${config.analyticsEnabled && (!fn:contains(pathPrefix,'subGroups'))}">
	<%-- Analytics data fetched asynchrounsly, hence this tag just acts as a place holder without any value --%>
	<div id="${csticValueKey}.analytics" class="cpq-csticValueAnalyticsTemplate cpq-media-width">
		<span id="${csticValueKey}.popularityTooltip" class="cpq-label-default" title="${popularityTooltip}">
			<span class="cpq-csticlabel-thumbs-up-icon" aria-hidden="true" role="presentation" ></span>
			<span id="${csticValueKey}.popularityInPercent" class="cpq-popularity-in-percent">${popularityInPercent}</span>
			<span id="${csticValueKey}.popularityAriaText" class="sr-only" role="tooltip">${popularityTooltip}</span>
		</span>
	</div>
</c:if>