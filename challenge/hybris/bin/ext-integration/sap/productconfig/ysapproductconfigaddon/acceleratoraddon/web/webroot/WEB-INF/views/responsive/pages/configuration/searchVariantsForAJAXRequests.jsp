<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="start:configVariantSearchResults" />
<div id="configVariantSearchResults" class="cpq-vc-container col-xs-12">
	<c:if test="${fn:length(variantSearchResult) > 0}">
		<div class="cpq-vc-title">
			<spring:message code="sapproductconfig.vc.preconfiguredVersions" text="Pre-configured Versions" />
		</div>
		<config:variantsCarousel variants="${variantSearchResult}" />
	</c:if>
</div>
<div id="end:configVariantSearchResults" />