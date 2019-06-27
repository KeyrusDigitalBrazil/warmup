<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="overview" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/overview"%>

<c:set var="variantOverview" value="${overviewUiData.overviewMode eq 'VARIANT_OVERVIEW'}" />
<c:set var="quotationVariantOverview" value="${overviewUiData.overviewMode eq 'QUOTATION_VARIANT_OVERVIEW'}" />
<c:set var="orderVariantOverview" value="${overviewUiData.overviewMode eq 'ORDER_VARIANT_OVERVIEW'}" />
<c:set var="savedCartVariantOverview" value="${overviewUiData.overviewMode eq 'SAVED_CART_VARIANT_OVERVIEW'}"/>
<c:set var="anyVariantOverview" value="${variantOverview or quotationVariantOverview or orderVariantOverview or savedCartVariantOverview}"/>

<div id="${fn:toLowerCase(overviewUiData.overviewMode)}" class="cpq-form cpq-overview">
	<form:form id="overviewform" method="POST" modelAttribute="overviewUiData">
      
		 <div class="cpq-groups">					
	
			<c:set value="${fn:length(overviewUiData.groups)}"  var="numberOfCsticGroups"/>
			<c:if test="${numberOfCsticGroups == 0}">
				<overview:noResult/>
			</c:if>	
				
			<c:forEach var="group" items="${overviewUiData.groups}" varStatus="groupStatus">
			<c:set value="groups[${groupStatus.index}]." var="path" />
				<overview:groupOverview group="${group}" level="1" variantOverview ="${anyVariantOverview}"/>
			</c:forEach>				
		</div>
		
		<form:input type="hidden" value="${overviewUiData.configId}" path="configId" />
		<form:input type="hidden" value="${overviewUiData.productCode}" path="productCode" />
		<form:input type="hidden" value="${overviewUiData.overviewMode}" path="overviewMode" />
		<form:input type="hidden" value="${overviewUiData.sourceDocumentId}" path="sourceDocumentId" />
		<form:input type="hidden" value="${overviewUiData.cpqAction}" path="cpqAction" />
		<form:input type="hidden" value="${overviewUiData.abstractOrderCode}" path="abstractOrderCode" />
		<form:input type="hidden" value="${overviewUiData.abstractOrderEntryNumber}" path="abstractOrderEntryNumber" />		
	</form:form>
</div>

