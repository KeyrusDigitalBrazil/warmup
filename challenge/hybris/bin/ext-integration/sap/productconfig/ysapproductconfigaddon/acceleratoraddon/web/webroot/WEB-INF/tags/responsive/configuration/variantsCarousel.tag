<%@ tag language="java" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="variants" required="true" type="java.util.List"%>

<div id="cpqVariantCarousel" class="cpq-variant-carousel carousel__component--carousel">
	<c:forEach var="variant" items="${variants}">
		<div class="carousel__item">
			<div class="cpq-vc-item-content">
				<div class="cpq-vc-imgContainer carousel__item--thumb">
					<img id="${variant.productCode}" src="${variant.imageData.url}" alt="${variant.name}" title="${variant.name}" />
				</div>
				<div class="carousel__item--name">${variant.name}${status.index}</div>
				<div class="carousel__item--price">${variant.price.formattedValue}</div>
			</div>
			<div class="cpq-vc-actions">
				<c:choose>
					<c:when test="${variant.cartEntryNumber != null}">
						<spring:url value="/${variant.productCode}/${variant.cartEntryNumber}/variantOvFromCartBound" var="variantOverviewUrl" />
					</c:when>
					<c:otherwise>	
						<spring:url value="/${variant.productCode}/variantOverview" var="variantOverviewUrl" />
					</c:otherwise>		
				</c:choose>	
				<button type="submit" class="cpq-vc-viewDetails-btn" data-go-to-overview="${variantOverviewUrl}">
					<spring:message code="sapproductconfig.vc.viewDetails" text="View Details" />
				</button>
			</div>
		</div>
	</c:forEach>
</div>
