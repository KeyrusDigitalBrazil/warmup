<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="overview" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/overview"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:set var="configOverview" value="${not empty overviewUiData and overviewUiData.overviewMode eq 'CONFIGURATION_OVERVIEW'}"/>
<c:set var="variantOverview" value="${not empty overviewUiData and (overviewUiData.overviewMode eq 'VARIANT_OVERVIEW' or overviewUiData.overviewMode eq 'VARIANT_OVERVIEW_FROM_CART_BOUND_CONFIG')}"/>
<c:set var="quotationOverview" value="${not empty overviewUiData and overviewUiData.overviewMode eq 'QUOTATION_OVERVIEW'}"/>
<c:set var="quotationVariantOverview" value="${not empty overviewUiData and overviewUiData.overviewMode eq 'QUOTATION_VARIANT_OVERVIEW'}"/>
<c:set var="orderOverview" value="${not empty overviewUiData and overviewUiData.overviewMode eq 'ORDER_OVERVIEW'}"/>
<c:set var="orderVariantOverview" value="${not empty overviewUiData and overviewUiData.overviewMode eq 'ORDER_VARIANT_OVERVIEW'}"/>
<c:set var="savedCartOverview" value="${overviewUiData.overviewMode eq 'SAVED_CART_OVERVIEW'}"/>
<c:set var="savedCartVariantOverview" value="${overviewUiData.overviewMode eq 'SAVED_CART_VARIANT_OVERVIEW'}"/>

<c:set var="eitherOneorAnotherVariantOverview" value="${variantOverview || quotationVariantOverview || orderVariantOverview || savedCartVariantOverview}"/>
<c:set var="noneVariantOverview" value="${!(variantOverview) and !(quotationVariantOverview) and !(orderVariantOverview) and !(savedCartVariantOverview)}"/>
<c:set var="eitherOneOrAnotherOverview" value="${quotationOverview || quotationVariantOverview || orderOverview || orderVariantOverview || savedCartOverview || savedCartVariantOverview}"/>

<c:set var="pricing" value="${config.pricing}"/>
<c:set var="showSavings" value="${false}"/>

<c:if test="${not empty overviewUiData}">
	<c:set var="pricing" value="${overviewUiData.pricing}"/>
	<c:if test="${pricing.currentTotalSavings.value.unscaledValue() != 0}">
		<c:set var="showSavings" value="${true}"/>
	</c:if>
</c:if>

<c:if test="${variantOverview || configOverview}">
	<div id="backToConfiguration" class="visible-xs cpq-backToConfig  cpq-addToCart">
		<overview:backToConfigButton />
	</div>
</c:if>

<div id="priceSummary" class="cpq-price-sum col-xs-12 col-sm-6 col-sm-offset-6">
	<c:if test="${showPriceDetails and !config.priceSummaryCollapsed and noneVariantOverview}">
			<div id="priceSummarySubContent" class="cpq-price-sum-sub">
				<div id="basePriceLabel" class="cpq-price-label" >
					<spring:theme code="sapproductconfig.pricesummary.label.baseprice" text="Base Price (Default)" />
				</div>
				<div id="basePriceValue" class="cpq-price-value" aria-describedby="basePriceLabel">${pricing.basePrice.formattedValue}</div>
				<div id="selectedOptionsLabel" class="cpq-price-label">
					<spring:theme code="sapproductconfig.pricesummary.label.selectedoptionsprice" text="Selected Options (Default)" />
				</div>
				<div id="selectedOptionsValue" class="cpq-price-value" aria-describedby="selectedOptionsLabel">${pricing.selectedOptions.formattedValue}</div>
				<div id="savings" class="cpq-price-savings"  <c:if test="${!showSavings}">style="display:none"</c:if>>
					<div id="savingsLabel" class="cpq-price-label">
						<spring:theme code="sapproductconfig.pricesummary.label.savings" text="Savings (Default)"/>
					</div>
					<div id="savingsValue" class="cpq-price-value"
						 aria-describedby="savingsLabel">${pricing.currentTotalSavings.formattedValue}</div>
				</div>
			</div>
	</c:if>
	<div class="cpq-price-sum-total">
		<div id="priceTotalLabel" class="cpq-price-label cpq-price-total">
			<spring:theme code="sapproductconfig.pricesummary.label.totalprice" text="Current Total (Default)"/>
		</div>
		<c:choose>
			<c:when test="${eitherOneorAnotherVariantOverview}">
				<div id="currentTotalValue" class="cpq-price-value cpq-price-total"
					 aria-describedby="priceTotalLabel">${product.price.formattedValue}</div>
			</c:when>
			<c:otherwise>
				<div id="currentTotalValue" class="cpq-price-value cpq-price-total"
					 aria-describedby="priceTotalLabel">${pricing.currentTotal.formattedValue}</div>
			</c:otherwise>
		</c:choose>

		<div class="cpq-price-label cpq-price-other">
			<spring:theme code="sapproductconfig.pricesummary.label.other" text=""/>
		</div>
	</div>
</div>

<c:if test="${variantOverview || configOverview}">
	<div id="backToConfiguration" class="hidden-xs col-sm-6 cpq-backToConfig">
		<overview:backToConfigButton />
	</div>
</c:if>


<div id="addToCartCol" class="cpq-addToCart col-xs-12 col-sm-6 <c:if test="${empty overviewUiData || eitherOneOrAnotherOverview}">col-sm-offset-6</c:if>" aria-describedby="priceTotalLabel currentTotalValue">
	<c:choose>
		<c:when test="${configOverview}">
			<overview:goToCartButton />
		</c:when>
		<c:when test="${variantOverview}">
			<overview:addVariantToCartButton />
		</c:when>
		<c:when test="${quotationOverview || quotationVariantOverview}">
			<overview:backToQuotationButton />
		</c:when>
		<c:when test="${orderOverview || orderVariantOverview}">
			<overview:backToOrderButton />
		</c:when>
		<c:when test="${savedCartOverview || savedCartVariantOverview}">
			<overview:backToSavedCartButton />
		</c:when>
		<c:otherwise>
			<c:set var="bindResult" value="${requestScope['org.springframework.validation.BindingResult.config']}" />
			<config:addToCartButton product="${product}" bindResult="${bindResult}" linkedToCartItem="${config.linkedToCartItem}"/>
		</c:otherwise>
	</c:choose>
</div>


