<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/checkout/multi/tax-invoice/add" var="submitUrl" htmlEscape="false"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	
	<div class="row">
		<div class="col-sm-6">
			<div class="checkout-headline">
		<spring:theme code="checkout.multi.secure.checkout" text="Secure Checkout"></spring:theme>
	</div>
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
				<ycommerce:testId code="checkoutStepThree">
					<div class="checkout-shipping">
						<div class="checkout-indent">
							<div class="headline"><spring:theme code="checkout.multi.taxInvoice.stepHeader" /></div>
							<div class="form-group">
								<label class="control-label "><spring:theme code="invoice.required"/></label>
								<div class="controls">
									<label>
										<input type="radio" id="invoiceRequiredYes" name="invoiceProvide" ${invoiceForm.invoiceRequired ? 'checked="checked"' : ''}/>
										<spring:theme code="invoice.required.yes" />
									</label>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<label>
										<input type="radio" id="invoiceRequiredNo" name="invoiceProvide" ${invoiceForm.invoiceRequired ? '' : 'checked="checked"'}/>
										<spring:theme code="invoice.required.no" />
									</label>
								</div>
							</div>
							<form:form commandName="invoiceForm" method="post" action="${submitUrl}">
								<input type="hidden" name="id" value="${fn:escapeXml(invoiceForm.id)}"/>
								<input type="hidden" name="invoiceRequired" id="invoiceRequired" value="${fn:escapeXml(invoiceForm.invoiceRequired)}"/>
								<div class="form-group">
									<template:errorSpanField path="recipientType">
										<ycommerce:testId code="LoginPage_Item_recipientType">
											<label class="control-label ">
												<spring:theme code="invoice.recipientType"/>
												<span class="mandatory"><spring:theme code="login.required" var="loginRequiredText" /></span>
												<span class="skip"><form:errors path="recipientType"/></span>
											</label>
											<div class="controls">
												<select ${invoiceForm.invoiceRequired ? '' : 'disabled="disabled"'} name="recipientType" id="invoiceTitle" class="form-control">
													<c:forEach items="${recipientTypes}" var="recipientType">
														<option value="${fn:escapeXml(recipientType.code)}" ${(recipientType.code eq "INDIVIDUAL" && empty invoiceForm.recipientType) ? 'selected="selected"' : ''} ${recipientType.code eq invoiceForm.recipientType ? 'selected="selected"' : ''}><spring:theme code='type.enum.invoice.recipientType.${recipientType.code}.code'/></option>
													</c:forEach>	
												</select>
											</div>
										</ycommerce:testId>
									</template:errorSpanField>
								</div>
								<div class="form-group" id="invoiceNamePanel" >
									<template:errorSpanField path="recipient">
										<ycommerce:testId code="LoginPage_Item_recipient">
											<label class="control-label ">
												<span class="skip"><form:errors path="recipient"/></span>
											</label>
											<div class="controls">
												<input type="text" value="${fn:escapeXml(invoiceForm.recipient)}"  class="form-control" style="${fn:escapeXml(selectCSSClass)}" ${invoiceForm.invoiceRequired ? '' : 'disabled="disabled"'}
												 id="invoiceName" name="recipient" placeholder="<spring:theme code='invoice.recipient.placeholder.value'/>">
											</div>
										</ycommerce:testId>
									</template:errorSpanField>
								</div>
								<div class="form-group">
									<template:errorSpanField path="category">
										<ycommerce:testId code="LoginPage_Item_">
											<label class="control-label ">
												<spring:theme code="invoice.category"/>
												<span class="mandatory"><spring:theme code="login.required" var="loginRequiredText" /></span>
												<span class="skip"><form:errors path="category"/></span>
											</label>
											<div class="controls">
												<select name="category" ${invoiceForm.invoiceRequired ? '' : 'disabled="disabled"'} id="invoiceCategory" class="form-control">
													<c:forEach items="${invoiceCategories}" var="category">
														<option value="${fn:escapeXml(category.code)}" ${(category.code eq "GENERAL" && empty invoiceForm.category) ? 'selected="selected"' : ''} ${category.code eq invoiceForm.category ? 'selected="selected"' : ''}><spring:theme code='type.enum.invoice.category.${category.code}.code'/></option>
													</c:forEach>	
												</select>
											</div>
										</ycommerce:testId>
									</template:errorSpanField>
								</div>
								<button class="btn btn-primary btn-block checkout-next"><spring:theme code="checkout.multi.deliveryMethod.continue" text="Next"/></button>
							</form:form>
						</div>
					</div>
				</ycommerce:testId>
			</jsp:body>
		</multi-checkout:checkoutSteps>
		</div>
		<div class="col-sm-6 hidden-xs">
		<multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
		</div>
		<div class="col-sm-12 col-lg-12">
			<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>

</template:page>
