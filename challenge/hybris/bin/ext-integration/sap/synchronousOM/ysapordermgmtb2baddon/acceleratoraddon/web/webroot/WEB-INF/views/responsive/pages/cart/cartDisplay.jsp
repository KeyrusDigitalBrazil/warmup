<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="addon" tagdir="/WEB-INF/tags/addons/ysapordermgmtb2baddon/responsive/cart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<spring:htmlEscape defaultHtmlEscape="true" />


<h1 class="cart-headline border">
	<spring:theme code="text.cart"/>
    <c:if test="${not empty cartData.code}">
        <span class="cart-id-label">
            <spring:theme code="basket.page.cartIdShort"/><span class="cart-id">${cartData.code}</span>
        </span>
    </c:if>
 
</h1>


<c:if test="${not empty cartData.entries}">
    <c:url value="/cart/checkout" var="checkoutUrl" scope="session"/>
    <c:url value="${continueUrl}" var="continueShoppingUrl" scope="session"/>
    <c:set var="showTax" value="true"/>
	
    <div class="js-cart-top-totals cart-top-totals">
        <c:choose>
            <c:when test="${fn:length(cartData.entries) > 1}">
                <spring:theme code="basket.page.totals.total.items" arguments="${fn:length(cartData.entries)}"/>
            </c:when>
            <c:otherwise>
                <spring:theme code="basket.page.totals.total.items.one" arguments="${fn:length(cartData.entries)}"/>
            </c:otherwise>
        </c:choose>
        <ycommerce:testId code="cart_totalPrice_label">
            <span class="cart-top-totals-amount">
                <c:choose>
                    <c:when test="${showTax}">
                        <format:price priceData="${cartData.totalPriceWithTax}"/>
                    </c:when>
                    <c:otherwise>
                        <format:price priceData="${cartData.totalPrice}"/>
                    </c:otherwise>
                </c:choose>
            </span>
        </ycommerce:testId>
    </div>

    <div class="row">
        <div class="col-xs-12 col-md-7 col-lg-6 pull-right">
            <div class="row cart-actions">
                <div class="col-sm-4 pull-right">
                    <ycommerce:testId code="checkoutButton">
                        <button class="btn btn-primary btn-block btn--continue-checkout js-continue-checkout-button" <c:if test="${messageFlagError == true}"><c:out value="disabled='disabled'"/></c:if>  data-checkout-url="${checkoutUrl}">
                            <spring:theme code="checkout.checkout"/>
                        </button>
                    </ycommerce:testId>
                </div>

                <div class="col-sm-5 pull-right">
                    <button class="btn btn-default btn-block btn--continue-shopping js-continue-shopping-button" data-continue-shopping-url="${continueShoppingUrl}">
                        <spring:theme text="Continue Shopping" code="cart.page.continue"/>
                    </button>
                </div>

            </div>
        </div>
    </div>
    
    <addon:cartItems cartData="${cartData}"/>
</c:if>
<cart:ajaxCartTopTotalSection/>
