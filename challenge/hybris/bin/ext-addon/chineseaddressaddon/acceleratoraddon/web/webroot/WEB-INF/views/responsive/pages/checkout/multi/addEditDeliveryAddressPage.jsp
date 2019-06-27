<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/addons/chineseaddressaddon/responsive/address" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	
	<div class="row">
	<div class="col-sm-6">
		<div class="checkout-headline"><spring:theme code="checkout.multi.secure.checkout" text="Secure Checkout"></spring:theme></div>
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
				<ycommerce:testId code="checkoutStepOne">
					<div class="checkout-shipping">
							<multi-checkout:shipmentItems cartData="${cartData}" showDeliveryAddress="false" />

							<div class="checkout-indent">
								<div class="headline"><spring:theme code="checkout.summary.shippingAddress" text="Shipping Address"></spring:theme></div>


									<address:addressFormSelector supportedCountries="${countries}"
										regions="${regions}" cancelUrl="${currentStepUrl}"
										country="${country}" />

										<div id="addressbook">

                                            <spring:url var="selectDeliveryAddressUrl" value="/checkout/multi/delivery-address/select" htmlEscape="false" />

											<c:forEach items="${deliveryAddresses}" var="address" varStatus="status">
												<div class="addressEntry">
													<form action="${fn:escapeXml(selectDeliveryAddressUrl)}" method="GET">
														<input type="hidden" name="selectedAddressCode" value="${fn:escapeXml(address.id)}" />
														<br/>${fn:escapeXml(address.fullnameWithTitle)}
														<%@ include file="/WEB-INF/views/addons/chineseaddressaddon/responsive/fragments/address/addressBookEntry.jsp" %>
														<button type="submit" class="btn btn-primary btn-block">
															<spring:theme
																code="checkout.multi.deliveryAddress.useThisAddress" text="Use this Address" />
														</button>
													</form>
												</div>
											</c:forEach>
										</div>

										<address:suggestedAddresses selectedAddressUrl="/checkout/multi/delivery-address/select" />
							</div>

								<multi-checkout:pickupGroups cartData="${cartData}" />
					</div>


					<button id="addressSubmit" type="button"
						class="btn btn-primary btn-block checkout-next"><spring:theme code="checkout.multi.deliveryAddress.continue" text="Next"/></button>
				</ycommerce:testId>
			</jsp:body>


			
		</multi-checkout:checkoutSteps>
		</div>
		
		<div class="col-sm-6 hidden-xs">
		<multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="false" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
		</div>
		
		<div class="col-sm-12 col-lg-12">
			<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>

</template:page>
