<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
<div class="row">
    <div class="col-sm-6">
        <div class="checkout-headline">
            <span class="glyphicon glyphicon-lock"></span>
            <spring:theme code="checkout.multi.secure.checkout"/>
        </div>
		<multiCheckout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
			
			
                    <div class="checkout-paymentmethod">
                        <div class="checkout-indent">

                            <div class="headline"><spring:theme code="checkout.multi.paymentMethod"/></div>

							    <ycommerce:testId code="paymentDetailsForm">
							
								<form:form id="js-new-card-register-form" name="newCardRegisterCard" action="${request.contextPath}/checkout/multi/sap-digital-payment/cards/new-card" method="GET">
									<div class="form-group">
										<c:if test="${not empty paymentInfos}">
											<button type="button" class="btn btn-default btn-block js-saved-payments"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useSavedCard"/></button>
										</c:if>	
									</div>
									
									 <button type="button" class="btn btn-primary btn-block js-checkout-payment-add-newcard"><spring:theme code="checkout.multi.paymentMethod.add.newcard"/></button>
									<p class="help-block"><spring:theme code="checkout.multi.paymentMethod.seeOrderSummaryForMoreInformation"/></p>							
								
								</form:form>
								
								
								
							</ycommerce:testId>
                         </div>
                    </div>
                   
                    
					<form:form id="js-new-card-poll-form" name="newCardPollForm"  action="${request.contextPath}/checkout/multi/sap-digital-payment/cards/poll" method="GET">
                    <button type="button" class="btn btn-primary btn-block submit_newCardPollForm checkout-next"><spring:theme code="checkout.multi.paymentMethod.continue"/></button>
                    </form:form>

				<c:if test="${not empty paymentInfos}">
					<div id="savedpayments">
						<div id="savedpaymentstitle">
							<div class="headline">
								<span class="headline-text"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useSavedCard"/></span>
							</div>
						</div>
						<div id="savedpaymentsbody">
							<c:forEach items="${paymentInfos}" var="paymentInfo" varStatus="status">
								<div class="saved-payment-entry">
									<form action="${request.contextPath}/checkout/multi/payment-method/choose" method="GET">
										<input type="hidden" name="selectedPaymentMethodId" value="${fn:escapeXml(paymentInfo.id)}"/>
											<ul>
												<strong>${fn:escapeXml(paymentInfo.billingAddress.firstName)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.lastName)}</strong><br/>
												${fn:escapeXml(paymentInfo.cardTypeData.name)}<br/>
												${fn:escapeXml(paymentInfo.cardNumber)}<br/>
												<spring:theme code="checkout.multi.paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(paymentInfo.expiryMonth)},${fn:escapeXml(paymentInfo.expiryYear)}"/><br/>
												${fn:escapeXml(paymentInfo.billingAddress.line1)}<br/>
												${fn:escapeXml(paymentInfo.billingAddress.town)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.region.isocodeShort)}<br/>
												${fn:escapeXml(paymentInfo.billingAddress.postalCode)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.country.isocode)}<br/>
											</ul>
											<button type="submit" class="btn btn-primary btn-block" tabindex="${(status.count * 2) - 1}"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useThesePaymentDetails"/></button>
									</form>
								</div>
							</c:forEach>
						</div>
					</div>
				</c:if>	
				
				
				

		   </jsp:body>
		</multiCheckout:checkoutSteps>
	</div>

	<div class="col-sm-6 hidden-xs">
		<multiCheckout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
    </div>

    <div class="col-sm-12 col-lg-12">
        <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </div>
</div>

</template:page>
