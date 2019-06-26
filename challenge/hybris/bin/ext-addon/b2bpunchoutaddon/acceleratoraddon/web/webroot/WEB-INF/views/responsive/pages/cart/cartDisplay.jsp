<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="addoncart" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/responsive/cart" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty cartData.entries}">
    <c:url value="/cxml/requisition" context="${originalContextPath}/punchout" var="requisitionUrl"/>
    <c:url value="/cxml/cancel" context="${originalContextPath}/punchout" var="cancelUrl"/>
    <c:url value="${continueUrl}" var="continueShoppingUrl" scope="session"/>
    
    <c:set var="showTax" value="false"/>
    <div class="js-cart-top-totals cart-top-totals">
        <c:choose>
            <c:when test="${fn:length(cartData.entries) > 1}">
                <spring:theme code="basket.page.totals.total.items" arguments="${fn:length(cartData.entries)}" />
            </c:when>
            <c:otherwise>
                <spring:theme code="basket.page.totals.total.items.one" arguments="${fn:length(cartData.entries)}" />
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
    <div class="cart__actions">
        <div class="row">
            <c:choose>
                <c:when test="${isInspectOperation}">
                    <div class="col-sm-5 col-md-3 col-sm-push-7 col-md-push-9">
                        <addoncart:returnButton url="${requisitionUrl}" />
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="col-sm-4 col-md-3 col-lg-2 col-md-push-4 col-lg-push-5">
                        <button class="btn btn-default btn-block btn--continue-shopping js-continue-shopping-button" data-continue-shopping-url="${fn:escapeXml(continueShoppingUrl)}">
                            <spring:theme code="cart.page.continue"/>
                        </button>
                    </div>
                    <div class="col-sm-3 col-md-2 col-md-push-4 col-lg-push-5">
                        <addoncart:cancelButton url="${cancelUrl}" />
                    </div>
                    <div class="col-sm-5 col-md-3 col-md-push-4 col-lg-push-5">
                        <addoncart:returnButton url="${requisitionUrl}" />
                    </div>
                </c:otherwise>
            </c:choose>
            </div>
            </div>

	<c:choose>
		<c:when test="${isInspectOperation}">
			<addoncart:cartItems cartData="${cartData}"/>
		</c:when>
		<c:otherwise>
	    	<cart:cartItems cartData="${cartData}"/>
	    </c:otherwise>
    </c:choose>

</c:if>
<cart:ajaxCartTopTotalSection/>
