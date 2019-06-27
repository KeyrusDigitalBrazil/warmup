<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="variantOverview" value="${overviewUiData.overviewMode eq 'VARIANT_OVERVIEW'}"/>
<c:set var="variantOverviewFromCartBound" value="${overviewUiData.overviewMode eq 'VARIANT_OVERVIEW_FROM_CART_BOUND_CONFIG'}"/>
<c:set var="configOverview" value="${overviewUiData.overviewMode eq 'CONFIGURATION_OVERVIEW'}"/>
<c:set var="quotationOverview" value="${overviewUiData.overviewMode eq 'QUOTATION_OVERVIEW'}"/>
<c:set var="quotationVariantOverview" value="${overviewUiData.overviewMode eq 'QUOTATION_VARIANT_OVERVIEW'}"/>
<c:set var="orderOverview" value="${overviewUiData.overviewMode eq 'ORDER_OVERVIEW'}"/>
<c:set var="orderVariantOverview" value="${overviewUiData.overviewMode eq 'ORDER_VARIANT_OVERVIEW'}"/>
<c:set var="savedCartOverview" value="${overviewUiData.overviewMode eq 'SAVED_CART_OVERVIEW'}"/>
<c:set var="savedCartVariantOverview" value="${overviewUiData.overviewMode eq 'SAVED_CART_VARIANT_OVERVIEW'}"/>
<c:set var="displayErrorPanel" value="${variantOverview or configOverview}"/>

<c:if test="${displayErrorPanel and (not empty config and (not config.consistent or not config.complete))}">
	<div class="cpq-overview-error-panel">
		<div class="cpq-overview-error-sign">&#xe101;</div>
		<div class="cpq-overview-error-message">
			<span><spring:message code="sapproductconfig.cart.entrytext.conflicts.responsive"
					text="{0} issues must be resolved before checkout" arguments="${errorCount}" var="errorMessage" />&nbsp;${errorMessage}<spring:url
					value="/cart/${overviewUiData.cartEntryNumber}/configuration/CPQCONFIGURATOR" var="resolveConfigUrl"></spring:url> 
					<spring:message code="sapproductconfig.addtocart.resolve.button" text="Resolve Issues Now"  var="errorMessageLink"/>
					<a href="${resolveConfigUrl}" aria-label="${errorMessage} &nbsp; ${errorMessageLink}">
						${errorMessageLink}
					</a>
			</span>
		</div>
	</div>
</c:if>

<c:choose>
	<c:when test="${variantOverview}">
		<spring:url value="/${product.baseProduct}/configuratorPage/CPQCONFIGURATOR" var="backUrl" />
		<spring:theme code="sapproductconfig.overview.variant.title" text="Review Variant (Default)" var="linkText" />
		<c:set var="backTo" value="backToConfiguration"/>
	</c:when>
	<c:when test="${quotationOverview or quotationVariantOverview}">
		<spring:url value="/my-account/my-quotes/${overviewUiData.sourceDocumentId}" var="backUrl" />
		<spring:theme code="sapproductconfig.overview.quotation.title" text="Review Selections for Quotation (Default)" var="linkText" />
		<c:set var="backTo" value="cpqBackToQuotationBtn"/>
	</c:when>
	<c:when test="${orderOverview or orderVariantOverview}">
		<spring:url value="/my-account/order/${overviewUiData.sourceDocumentId}" var="backUrl" />
		<spring:theme code="sapproductconfig.overview.order.title" text="Review Selections for Order (Default)" var="linkText" />
		<c:set var="backTo" value="cpqBackToOrderBtn"/>
	</c:when>
	<c:when test="${savedCartOverview or savedCartVariantOverview}">
		<spring:url value="/my-account/saved-carts/${overviewUiData.sourceDocumentId}" var="backUrl" />
		<spring:theme code="sapproductconfig.overview.savedCart.title" text="Review Selections for Cart (Default)" var="linkText" />
		<c:set var="backTo" value="cpqBackToSavedCartBtn"/>
	</c:when>	
	<c:when test="${variantOverviewFromCartBound}">
		<spring:url value="/cart/${overviewUiData.cartEntryNumber}/configureOnDraft/CPQCONFIGURATOR" var="backUrl" />
		<spring:theme code="sapproductconfig.overview.title" text="Review Selections for Cart (Default)" var="linkText" />
		<c:set var="backTo" value="cpqBackToSavedCartBtn"/>
	</c:when>		
	<c:otherwise>
		<spring:url value="/cart/${overviewUiData.cartEntryNumber}/configuration/CPQCONFIGURATOR" var="backUrl" />
		<spring:theme code="sapproductconfig.overview.title" text="Review your Selections (Default)"  var="linkText"/>
		<c:set var="backTo" value="backToConfiguration"/>
	</c:otherwise>
</c:choose>

<div class="back-link border">
	<button type="button" class="cpq-back-button" data-back-to-url="${backUrl}" aria-describedby="${backTo}">
		<span class="glyphicon glyphicon-chevron-left"></span>
	</button>
	<span id="backButtonLinkText" class="label">${linkText}</span>
</div>
