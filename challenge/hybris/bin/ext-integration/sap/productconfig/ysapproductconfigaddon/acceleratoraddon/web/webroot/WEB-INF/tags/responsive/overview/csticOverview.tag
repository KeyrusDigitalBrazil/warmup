<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="confUtil" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>
<%@ taglib prefix="overview" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/overview" %>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue"%>

<div class="cpq-overview-cstic-row">
	<div class="col-xs-9">
		<div class="cpq-overview-cstic-value">
			<c:choose>
				<c:when test="${cstic.value eq confUtil:notImplemented()}">
					<spring:message
					code="sapproductconfig.cstic.type.notimplemented"
					text="Not implemented" />
				</c:when>
				<c:otherwise>
					<c:out value="${cstic.value}" />
				</c:otherwise>
			</c:choose>			
		</div>								
		<div class="cpq-overview-cstic-label hidden-xs hidden-sm">
			<label>${cstic.characteristic}</label>
		</div>			
		<div class="cpq-overview-cstic-label visible-xs visible-sm">
			<overview:promoMessage messages="${cstic.messages}"/>
			<label>${cstic.characteristic}</label>
		</div>
	</div>

	<overview:valuePrice cstic="${cstic}"/>
	<div class="hidden-xs hidden-sm col-md-9">
		<overview:promoMessage messages="${cstic.messages}"/>
	</div>
</div>

