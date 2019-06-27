<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="/checkout/multi/summary/checkPaymentResult/{/code}" var="resultUrl" htmlEscape="false">
	<spring:param name="code"  value="${orderData.code}"/>
</spring:url>

<div class="account-orderdetail well well-tertiary account-consignment">
    <div class="well-headline">
        <spring:theme code="text.paymentDetails" text="Payment Details" />
    </div>
    <ycommerce:testId code="orderDetails_paymentDetails_section">
	    <div class="well-content col-sm-12 col-md-9">
	        <div class="row">
	            <div class="col-sm-6 col-md-4 order-payment-data">
	                <div class="label-order">
	                    <spring:theme code="account.order.detail.payment.method.title" text="Payment Method" />
					</div>
					<div id="providerName" class="value-order">
	                    ${fn:escapeXml(orderData.chinesePaymentInfo.paymentProviderName)}&nbsp;<c:if test="${not empty orderData.chinesePaymentInfo.serviceType}">-&nbsp;${fn:escapeXml(orderData.chinesePaymentInfo.serviceType)}</c:if>
	                </div>
	            </div>
	            <div class="col-sm-6 col-md-4 order-payment-data">
	                <div class="label-order">
	                    <spring:theme code="account.order.detail.payment.status.title" text="Payment Status" />
	                </div>
	                <div id="paymentStatus" class="value-order">
	                    <spring:theme code="type.PaymentStatus.${orderData.paymentStatus.code}.name" text="${orderData.paymentStatus.code}"/>
	                </div>
	            </div>
	            <div class="col-sm-6 col-md-4 order-payment-data">
			        <action:actions element="div" parentComponent="${component}"/>
	            </div>
	        </div>
	    </div>
    </ycommerce:testId>
</div>

<div class="mask"></div>
<div class="row pay-pop-container">
    <div class="payPop col-lg-4 col-sm-6 col-md-6 col-xs-12">
    	<p><spring:theme code="order.payment.remark" /></p>
        <div class="payPopBtn clearfix">
        	<a href="${fn:escapeXml(resultUrl)}" class="btn btn-primary btn-block checkout-next"><spring:theme code="order.payment.successfully" /></a><br/>
            <a href="${fn:escapeXml(resultUrl)}" class="btn btn-primary btn-block checkout-next"><spring:theme code="order.payment.rencontre.problem" /></a>
            <br><br>
        </div>	
    </div>
</div>
