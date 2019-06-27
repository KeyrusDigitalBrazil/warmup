<%@ tag language="java" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="valuePrice" required="true"
	type="de.hybris.platform.commercefacades.product.data.PriceData"%>
<%@ attribute name="selected" required="true" type="java.lang.Boolean"%>
<%@ attribute name="csticKey" required="true" type="java.lang.String"%>
<%@ attribute name="showDeltaPrice" required="true" type="java.lang.Boolean"%>

<c:if test="${valuePrice.formattedValue ne '-'}">
	<c:choose>
		<c:when test="${valuePrice.value.unscaledValue() == 0 && selected && showDeltaPrice}">
			<spring:message code="sapproductconfig.deltaprcices.selected" text="Selected" var="formattedPrice" />
		</c:when>
		<c:otherwise>
			<c:if test="${valuePrice.value.unscaledValue() != 0}">
				<c:choose>
					<c:when test="${fn:contains(valuePrice.formattedValue, '-')}">
						<c:set value="${valuePrice.formattedValue}" var="formattedPrice" />
					</c:when>
					<c:otherwise>
						<c:set value="+${valuePrice.formattedValue}" var="formattedPrice" />
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:otherwise>
	</c:choose>
	<div  class="cpq-valuePrices">
		<div id="${csticKey}.valuePrice" title="${formattedPrice}"
			 class="cpq-csticValueDeltaPrice cpq-csticValueLabel">${formattedPrice}</div>
   </div>
</c:if>
