<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="overview" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/overview"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="conf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<c:set var="notVariantOverview" value="${overviewUiData.overviewMode ne 'VARIANT_OVERVIEW'}" />
<c:set var="notQuotationVariantOverview" value="${overviewUiData.overviewMode ne 'QUOTATION_VARIANT_OVERVIEW'}" />
<c:set var="notOrderVariantOverview" value="${overviewUiData.overviewMode ne 'ORDER_VARIANT_OVERVIEW'}" />

<c:if test="${notVariantOverview and notQuotationVariantOverview and notOrderVariantOverview}">
	<div class="cpq-overview-filter">
		<div class="yComponentWrapper">
			<div id="cpq-overview-facet" class="col-md-12 hidden-sm hidden-xs product__facet cpq-overview-facet cpq-js-overview-facet">
				<form:form id="filterform" method="POST" modelAttribute="overviewUiData">
					<c:if test="${conf:hasAppliedFilters(overviewUiData)}">
						<overview:appliedFilters csticFilterList="${overviewUiData.csticFilterList}"
							groupFilterList="${overviewUiData.groupFilterList}" />
					</c:if>
					<overview:filterCstics csticFilterList="${overviewUiData.csticFilterList}" />
					<overview:filterGroups groupFilterList="${overviewUiData.groupFilterList}" />
					<form:input type="hidden" value="${overviewUiData.configId}" path="configId" />
					<form:input type="hidden" value="${overviewUiData.productCode}" path="productCode" />
					<form:input type="hidden" value="${overviewUiData.cpqAction}" path="cpqAction" id="filterCPQAction" />
					<form:input type="hidden" value="${overviewUiData.overviewMode}" path="overviewMode" />
					<form:input type="hidden" value="${overviewUiData.sourceDocumentId}" path="sourceDocumentId" />
					<form:input type="hidden" value="${overviewUiData.abstractOrderCode}" path="abstractOrderCode" />
					<form:input type="hidden" value="${overviewUiData.abstractOrderEntryNumber}" path="abstractOrderEntryNumber" />	
				</form:form>
			</div>
		</div>
	</div>
</c:if>