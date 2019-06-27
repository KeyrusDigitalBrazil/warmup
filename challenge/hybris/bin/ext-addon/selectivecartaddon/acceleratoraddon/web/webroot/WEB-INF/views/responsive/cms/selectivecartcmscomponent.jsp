<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="selectivecart" tagdir="/WEB-INF/tags/addons/selectivecartaddon/responsive/selectivecart" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/cart/entries/check" var="checkUrl" htmlEscape="false"/>
<spring:url value="/cart/entries/uncheck" var="uncheckUrl" htmlEscape="false"/>

<div class="cart-header border">
    <div class="row">
        <div class="col-xs-12 col-sm-5">
            <h1 class="cart-headline">
                <spring:theme code="text.cart"/>
                <c:if test="${not empty cartData.code}">
                    <span class="cart__id--label">
                        <spring:theme code="basket.page.cartIdShort"/><span class="cart__id">${fn:escapeXml(cartData.code)}</span>
                    </span>
                </c:if>
            </h1>
        </div>
        <div class="col-xs-12 col-sm-7">

            <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
                <c:if test="${not empty savedCartCount and savedCartCount ne 0}">
                    <spring:url value="/my-account/saved-carts" var="listSavedCartUrl" htmlEscape="false"/>
                    <a href="${fn:escapeXml(listSavedCartUrl)}" class="save__cart--link cart__head--link">
                        <spring:theme code="saved.cart.total.number" arguments="${savedCartCount}"/>
                    </a>
                    <c:if test="${not empty quoteCount and quoteCount ne 0}">
                        <spring:url value="/my-account/my-quotes" var="listQuotesUrl" htmlEscape="false"/>
                        <a href="${fn:escapeXml(listQuotesUrl)}" class="cart__quotes--link cart__head--link">
                            <spring:theme code="saved.quote.total.number" arguments="${quoteCount}"/>
                        </a>
                    </c:if>

                </c:if>
            </sec:authorize>
            <cart:saveCart/>
        </div>
    </div>
</div>


<c:if test="${not empty cartData.rootGroups}">
    <spring:url value="/cart/checkout" var="checkoutUrl" scope="session" htmlEscape="false"/>
    <spring:url value="/quote/create" var="createQuoteUrl" scope="session" htmlEscape="false"/>
    <spring:url value="${continueUrl}" var="continueShoppingUrl" scope="session" htmlEscape="false"/>
    <c:set var="showTax" value="false"/>

    <div class="row">
        <div class="col-xs-12 pull-right cart-actions--print">
            <div class="cart__actions border">
                <div class="row">
                    <div class="col-sm-4 col-md-3 pull-right">
                        <ycommerce:testId code="checkoutButton">
                            <button class="btn btn-primary btn-block btn--continue-checkout js-continue-checkout-button" data-checkout-url="${fn:escapeXml(checkoutUrl)}">
                                <spring:theme code="checkout.checkout"/>
                            </button>
                        </ycommerce:testId>
                    </div>

                    <c:if test="${not empty siteQuoteEnabled and siteQuoteEnabled eq 'true'}">
                        <div class="col-sm-4 col-md-3 col-md-offset-3 pull-right">
                            <button class="btn btn-default btn-block btn-create-quote js-create-quote-button" data-create-quote-url="${fn:escapeXml(createQuoteUrl)}">
                                <spring:theme code="quote.create"/>
                            </button>
                        </div>
                    </c:if>

                    <div class="col-sm-4 col-md-3 pull-right">
                        <button class="btn btn-default btn-block btn--continue-shopping js-continue-shopping-button" data-continue-shopping-url="${fn:escapeXml(continueShoppingUrl)}">
                            <spring:theme code="cart.page.continue"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

	<div class="row">
		<cart:exportCart/>
	</div>
	
    <div class="row">
    	<div class="col-xs-12 col-md-3 pull-left cart-select-all">
        	<form:form action="" autocomplete="off">
	        	<label class="cart-select-all-label">
	        		<c:choose>
		        	    <c:when test="${empty wishlistOrders}">
				       		<input type="checkbox" checked="checked" disabled="disabled" class="js-cart-select-all cart-select-checkbox" data-uncheckurl="${fn:escapeXml(uncheckUrl)}"/>
			        		<spring:theme code="wishlist2.action.selectall"/>
		        		</c:when>
		        		<c:otherwise>
		        			<input type="checkbox" disabled="disabled" class="js-cart-select-all cart-select-checkbox" data-checkurl="${fn:escapeXml(checkUrl)}"/>
			        		<spring:theme code="wishlist2.action.selectall"/>
		        		</c:otherwise>
	        		</c:choose>
	        	</label>
        	</form:form>
       	</div>
        
        <div class="col-sm-12 col-md-4 col-md-push-5">
            <div class="js-cart-top-totals cart__top--totals">
                <c:choose>
                    <c:when test="${fn:length(cartData.entries) > 1 or fn:length(cartData.entries) == 0}">
                        <spring:theme code="basket.page.totals.total.items" arguments="${fn:length(cartData.entries)}"/>
                    </c:when>
                    <c:otherwise>
                        <spring:theme code="basket.page.totals.total.items.one" arguments="${fn:length(cartData.entries)}"/>
                    </c:otherwise>
                </c:choose>
                <ycommerce:testId code="cart_totalPrice_label">
            <span class="cart__top--amount">
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
        </div>
    </div>

    <selectivecart:cartItems cartData="${cartData}"/>
    
    <div class="row">
	    <div class="col-xs-12 col-md-3 pull-left cart-select-all">
	    	<form:form action="" autocomplete="off">
		     	<label class="cart-select-all-label">
		     		<c:choose>
		      	    <c:when test="${empty wishlistOrders}">
		       			<input type="checkbox" checked="checked" disabled="disabled" class="js-cart-select-all cart-select-checkbox" data-uncheckurl="${fn:escapeXml(uncheckUrl)}"/>
		       			<spring:theme code="wishlist2.action.selectall"/>
		      		</c:when>
		      		<c:otherwise>
		      			<input type="checkbox" disabled="disabled" class="js-cart-select-all cart-select-checkbox" data-checkurl="${fn:escapeXml(checkUrl)}"/>
		       			<spring:theme code="wishlist2.action.selectall"/>
		      		</c:otherwise>
		     		</c:choose>
		     	</label>
	    	</form:form>
	   	</div>
   	</div>
    
    <div class="row">
        <cart:exportCart/>
    </div>
</c:if>

<c:if test="${empty cartData.rootGroups && not empty wishlistOrders}">
		<div class="row">
        <div class="col-xs-12 pull-right cart-actions--print">
            <div class="cart__actions border">
                <div class="row">
                    <div class="col-sm-4 col-md-3 pull-right">
                        <ycommerce:testId code="checkoutButton">
                            <button class="btn btn-primary btn-block btn--continue-checkout js-continue-checkout-button" data-checkout-url="${fn:escapeXml(checkoutUrl)}">
                                <spring:theme code="checkout.checkout"/>
                            </button>
                        </ycommerce:testId>
                    </div>

                    <c:if test="${not empty siteQuoteEnabled and siteQuoteEnabled eq 'true'}">
                        <div class="col-sm-4 col-md-3 col-md-offset-3 pull-right">
                            <button class="btn btn-default btn-block btn-create-quote js-create-quote-button" data-create-quote-url="${fn:escapeXml(createQuoteUrl)}">
                                <spring:theme code="quote.create"/>
                            </button>
                        </div>
                    </c:if>

                    <div class="col-sm-4 col-md-3 pull-right">
                        <button class="btn btn-default btn-block btn--continue-shopping js-continue-shopping-button" data-continue-shopping-url="${fn:escapeXml(continueShoppingUrl)}">
                            <spring:theme code="cart.page.continue"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
	
	<div class="row">
		<div class="col-xs-12 col-md-3 pull-left cart-select-all-wishlist">
			<form:form action="" autocomplete="off">
				<label class="cart-select-all-label">
					<input type="checkbox" disabled="disabled" class="js-cart-select-all cart-select-checkbox" data-checkurl="${fn:escapeXml(checkUrl)}"/>
					<spring:theme code="wishlist2.action.selectall"/>
				</label>
			</form:form>
		</div>
	</div>
	 
    <selectivecart:wishlist2Items wishlistOrders="${wishlistOrders}"/>
    
	<div class="row">
		<div class="col-xs-12 col-md-3 pull-left cart-select-all">
			<form:form action="" autocomplete="off">
				<label class="cart-select-all-label">
					<input type="checkbox" disabled="disabled" class="js-cart-select-all cart-select-checkbox" data-checkurl="${fn:escapeXml(checkUrl)}"/>
					<spring:theme code="wishlist2.action.selectall"/>
				</label>
			</form:form>
		</div>
	</div>
    
	<div class="row">
	    <div class="col-xs-12 col-sm-10 col-md-7 col-lg-6 pull-right cart-actions--print">
	        <div class="express-checkout">
	            <div class="headline"><spring:theme code="text.expresscheckout.header"/></div>
	            <strong><spring:theme code="text.expresscheckout.title"/></strong>
	            <ul>
	                <li><spring:theme code="text.expresscheckout.line1"/></li>
	                <li><spring:theme code="text.expresscheckout.line2"/></li>
	                <li><spring:theme code="text.expresscheckout.line3"/></li>
	            </ul>
	            <sec:authorize access="isFullyAuthenticated()">
	                <c:if test="${expressCheckoutAllowed}">
	                    <div class="checkbox">
	                        <label>
	                            <spring:url value="/checkout/multi/express" var="expressCheckoutUrl" scope="session" htmlEscape="false"/>
	                            <input type="checkbox" class="express-checkout-checkbox" data-express-checkout-url="${fn:escapeXml(expressCheckoutUrl)}">
	                            <spring:theme text="I would like to Express checkout" code="cart.expresscheckout.checkbox"/>
	                        </label>
	                     </div>
	                </c:if>
	           </sec:authorize>
	        </div>
	    </div>
	</div>

	<div class="cart__actions">
	    <div class="row">
	        <div class="col-sm-4 col-md-3 pull-right">
	            <ycommerce:testId code="checkoutButton">
	                <button class="btn btn-primary btn-block btn--continue-checkout js-continue-checkout-button" data-checkout-url="${fn:escapeXml(checkoutUrl)}">
	                    <spring:theme code="checkout.checkout"/>
	                </button>
	            </ycommerce:testId>
	        </div>
	
	        <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
	            <c:if test="${not empty siteQuoteEnabled and siteQuoteEnabled eq 'true'}">
	                <div class="col-sm-4 col-md-3 col-md-offset-3 pull-right">
	                    <button class="btn btn-default btn-block btn--continue-shopping js-continue-shopping-button"    data-continue-shopping-url="${fn:escapeXml(createQuoteUrl)}">
	                        <spring:theme code="quote.create"/>
	                    </button>
	                </div>
	            </c:if>
	        </sec:authorize>
	
	        <div class="col-sm-4 col-md-3 pull-right">
	            <button class="btn btn-default btn-block btn--continue-shopping js-continue-shopping-button" data-continue-shopping-url="${fn:escapeXml(continueShoppingUrl)}">
	                <spring:theme code="cart.page.continue"/>
	            </button>
	        </div>
	    </div>
	</div>
</c:if>
	


<cart:ajaxCartTopTotalSection/>