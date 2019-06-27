<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="${currentStepUrl}" var="choosePaymentMethodUrl" htmlEscape="false"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div class="row">
		<div class="col-sm-6">
			<div class="checkout-headline">
				<spring:theme code="checkout.multi.secure.checkout" />
			</div>
			<multiCheckout:checkoutSteps checkoutSteps="${checkoutSteps}"
				progressBarId="${progressBarId}">
				<jsp:body>
				<ycommerce:testId code="checkoutStepThree">
					<div class="checkout-indent">

						<div class="checkout-paymentmethod">
							<div class="headline">
									<spring:theme
										code="checkout.multi.paymentMethod.addPaymentDetails.paymentmode" />
								</div>
							<form:form method="post" action="${choosePaymentMethodUrl}"
									class="create_update_payment_form">
								<ul class="delivery_method-list">
									<c:forEach items="${paymentMethodsList}" var="paymentMethod"
											varStatus="index">
										<li id="${fn:escapeXml(paymentMethod.code)}">
											<label>
												<input type="radio" name="paymentId"
													value="${fn:escapeXml(paymentMethod.code)}"
													${(index.index==0 && empty cartData.chinesePaymentInfo.paymentProvider) ? "checked=checked":""}
													${cartData.chinesePaymentInfo.paymentProvider eq paymentMethod.code ? "checked=checked":""} /> 
												<img src="${fn:escapeXml(paymentMethod.logoUrl)}" alt="payment methods list" width="100px">
											</label>
										</li>
									</c:forEach>
								</ul>
								<ycommerce:testId
										code="editPaymentMethod_savePaymentMethod_button">
									<button
											class="btn btn-primary btn-block submit_silentOrderPostForm checkout-next">
										<spring:theme code="checkout.multi.paymentMethod.continue"
												text="Next" />
									</button>
								</ycommerce:testId>
							</form:form>
						</div>
					</div>
				</ycommerce:testId>
		   </jsp:body>
			</multiCheckout:checkoutSteps>
		</div>

		<div class="col-sm-6 hidden-xs">
			<multiCheckout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
		</div>
		<div class="col-sm-12 col-lg-12">
			<cms:pageSlot position="SideContent" var="feature" element="div"
				class="checkout-help">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>

	</div>

</template:page>
