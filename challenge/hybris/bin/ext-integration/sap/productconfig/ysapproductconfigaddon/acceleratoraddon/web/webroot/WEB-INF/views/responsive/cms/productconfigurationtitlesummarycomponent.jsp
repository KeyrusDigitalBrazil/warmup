<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="variantOverview" value="${overviewUiData.overviewMode eq 'VARIANT_OVERVIEW'}" />
<c:set var="quotationVariantOverview" value="${overviewUiData.overviewMode eq 'QUOTATION_VARIANT_OVERVIEW'}" />
<c:set var="orderVariantOverview" value="${overviewUiData.overviewMode eq 'ORDER_VARIANT_OVERVIEW'}" />
<c:set var="savedCartVariantOverview" value="${overviewUiData.overviewMode eq 'SAVED_CART_VARIANT_OVERVIEW'}"/>

<c:set var="anyVariantOverview" value="${variantOverview or quotationVariantOverview or orderVariantOverview or savedCartVariantOverview}"/>

<c:set var="imageVisible" value="${(not empty config and !config.hideImageGallery) or (not empty overviewUiData and anyVariantOverview)}"/>

<c:choose>
	<c:when test="${imageVisible}">
		<c:set value="open" var="chevronClass" />
	</c:when>
	<c:otherwise>
		<spring:url value="close" var="chevronClass" />
	</c:otherwise>
</c:choose>

<div class="product-details">
	<div id="productName" class="name product-details-glyphicon-chevron-${chevronClass}">${product.name}
		<span class="sku">ID ${product.code}</span>
	</div>
</div>


