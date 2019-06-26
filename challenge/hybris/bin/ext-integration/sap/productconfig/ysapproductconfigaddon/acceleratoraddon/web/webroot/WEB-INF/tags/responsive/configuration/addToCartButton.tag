<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="conf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="bindResult" required="true" type="org.springframework.validation.BindingResult"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="linkedToCartItem" required="false" type="java.lang.Boolean"%>

<c:set var="bindResult" value="${requestScope['org.springframework.validation.BindingResult.config']}" />
<c:set var="validationErrors" value="${conf:validationError(bindResult)}" />

<c:set var="noStock" value="true"/>
<c:if
	test="${((product.variantType ne null and product.purchasable eq false) or product.purchasable) 
				and product.stock.stockLevelStatus.code ne 'outOfStock'}">
	<c:set var="noStock" value="false"/>
</c:if>

<c:set var="disabled" value="" />
<c:if test="${fn:length(validationErrors) gt 0 or noStock}">
	<c:set var="disabled" value="disabled='disabled'" />
</c:if>

<spring:theme code="text.addToCart" var="buttonText"/>
<c:if test="${linkedToCartItem}">
	<spring:theme code="sapproductconfig.updateCart" var="buttonText" />
</c:if>

<button type="submit" id="cpqAddToCartBtn" <c:out value="${disabled}"/> class="cpq-btn-addToCart" aria-describedby="priceTotalLabel currentTotalValue">
	<c:out value="${buttonText}" />
</button>



