<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="cancel-panel col-xs-12 col-sm-12 col-md-10 col-lg-8">
	<div class="row">
	    <div class="col-sm-6">
            <ycommerce:testId code="orderDetails_backToOrderHistory_button">
                <spring:url value="/my-account/orders" var="orderHistoryUrl"/>
                <button type="button" class="btn btn-default orderBackBtn btn-block" data-back-to-orders="${orderHistoryUrl}">
                    <spring:theme code="text.account.orderDetails.backToOrderHistory"/>
                </button>
            </ycommerce:testId>
        </div>
    </div>
</div>