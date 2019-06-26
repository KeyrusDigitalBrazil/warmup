<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="addoncart" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/responsive/cart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:url value="/cxml/requisition" context="${originalContextPath}/punchout" var="requisitionUrl"/>
<c:url value="/cxml/cancel" context="${originalContextPath}/punchout" var="cancelUrl"/>

<div class="cart__actions">
    <div class="row-basket-bottom-punchout">
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
</div>