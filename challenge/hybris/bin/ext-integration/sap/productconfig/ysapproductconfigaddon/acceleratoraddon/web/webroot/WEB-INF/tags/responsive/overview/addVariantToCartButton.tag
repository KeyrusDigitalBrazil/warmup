<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:url value="/cart/add" var="addToCartUrl" />
<form:form id="addToCartForm${product.code}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
	<ycommerce:testId code="addToCartButton">
		<input type="hidden" name="productCodePost" value="${product.code}" />
		<input type="hidden" name="productNamePost" value="${product.name}" />
		<input type="hidden" name="productPostPrice" value="${product.price.value}" />
		<input type="hidden" id="qty" name="qty" value="${overviewUiData.quantity}" />
		<button
			class="btn btn-primary btn-block js-add-to-cart js-enable-btn btn-icon glyphicon-shopping-cart cpq-btn-addVariantToCartCleanUp
	            <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }"> out-of-stock</c:if>"
			<c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">aria-disabled="true"</c:if> disabled="disabled" aria-describedby="priceTotalLabel currentTotalValue">
			<spring:theme code="basket.add.to.basket" />
		</button>
	</ycommerce:testId>
</form:form>

<div id="addToCartTitle" class="display-none">
	<div class="add-to-cart-header">
		<div class="headline">
			<span class="headline-text"><spring:theme code="basket.added.to.basket" /></span>
		</div>
	</div>
</div>
