<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>

{"cartData": {
"total": "${cartData.totalPrice.value}",
"products": [
<c:forEach items="${cartData.entries}" var="cartEntry" varStatus="status">
	{
		"sku":		"${cartEntry.product.code}",
		"name": 	"<c:out value='${cartEntry.product.name}' />",
		"qty": 		"${cartEntry.quantity}",
		"price": 	"${cartEntry.basePrice.value}",
		"categories": [
		<c:forEach items="${cartEntry.product.categories}" var="category" varStatus="categoryStatus">
			"<c:out value='${category.name}' />"<c:if test="${not categoryStatus.last}">,</c:if>
		</c:forEach>
		]
	}<c:if test="${not status.last}">,</c:if>
</c:forEach>
]
},

"cartAnalyticsData":{"cartCode" : "${cartCode}","productPostPrice":"${entry.basePrice.value}","productName":"<c:out value='${product.name}' />"}
,
"addToCartLayer":"<spring:escapeBody javaScriptEscape="true">
	<spring:theme code="text.addToCart" var="addToCartText"/>
	<c:url value="/cart" var="cartUrl"/>
	<c:url value="/cart/checkout" var="checkoutUrl"/>
	<ycommerce:testId code="addToCartPopup">
		<div id="addToCartLayer" class="add-to-cart">
            <div class="cart_popup_error_msg">
                <c:choose>
                    <c:when test="${multidErrorMsgs ne null and not empty multidErrorMsgs}">
                        <c:forEach items="${multidErrorMsgs}" var="multidErrorMsg">
                            <spring:theme code="${multidErrorMsg}" />
                        </br>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <spring:theme code="${errorMsg}" />
                    </c:otherwise>
                </c:choose>
            </div>

            <c:choose>
                <c:when test="${modifications ne null}">
                    <c:forEach items="${modifications}" var="modification">
                        <c:set var="product" value="${modification.entry.product}" />
                        <c:set var="entry" value="${modification.entry}" />
                        <c:set var="quantity" value="${modification.quantityAdded}" />
                        <c:if test="${messageFlag eq true}">
							<cart:cartValidation/>
						</c:if>
                        <cart:popupCartItems entry="${entry}" product="${product}" quantity="${quantity}"/>
                    </c:forEach>
                </c:when>
                <c:otherwise>
					<c:if test="${messageFlag eq true}">
						<cart:cartValidation/>
					</c:if>
                    <cart:popupCartItems entry="${entry}" product="${product}" quantity="${quantity}"/>
                </c:otherwise>
            </c:choose>

            <ycommerce:testId code="checkoutLinkInPopup">
                <a href="${cartUrl}" class="btn btn-primary btn-block add-to-cart-button">
                    <spring:theme code="checkout.checkout" />
                </a>
            </ycommerce:testId>


            <a href="" class="btn btn-default btn-block js-mini-cart-close-button">
                <spring:theme code="cart.page.continue"/>
            </a>
		</div>
	</ycommerce:testId>
</spring:escapeBody>"
}



