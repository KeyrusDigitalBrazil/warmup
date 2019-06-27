<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="overview" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/overview" %>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue"%>

<c:if test="${cstic.valuePositionType eq 'FIRST'}">
	<div class="cpq-overview-cstic-row">
</c:if>
	<div class="cpq-overview-multi-valued-cstic-row">
		<div class="col-xs-9">
			<div class="cpq-overview-cstic-value">
				<c:out value="${cstic.value}" />
				<overview:promoMessage messages="${cstic.messages}"/>
			</div>			
			<c:if test="${cstic.valuePositionType eq 'FIRST'}">
			<div class="cpq-overview-cstic-label hidden-xs hidden-sm">
				<label>${cstic.characteristic}</label>
			</div>
		</c:if>
		<c:if test="${cstic.valuePositionType eq 'LAST'}">
			<div class="cpq-overview-cstic-label visible-xs visible-sm">
				<label>${cstic.characteristic}</label>
			</div>
		</c:if>
		</div>

		<overview:valuePrice cstic="${cstic}"/>
	</div>
<c:if test="${cstic.valuePositionType eq 'LAST'}">
	</div>
</c:if>
