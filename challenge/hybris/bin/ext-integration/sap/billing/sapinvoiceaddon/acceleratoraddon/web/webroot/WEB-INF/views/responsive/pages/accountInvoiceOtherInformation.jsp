<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<spring:htmlEscape defaultHtmlEscape="true" />


<div class="account-orderdetail well well-tertiary">
	<div class="well-headline orderPending">
		<spring:theme code="text.invoice.otherInformation" />
	</div>
	<ycommerce:testId code="orderDetails_paymentDetails_section">
		<div class="well-content">
			<div class="col-sm-12 invoice-other-information">
				<div class="label-order">
					<spring:theme code="text.account.invoice.order.conditions" />
				</div>
				<div class="invoice-terms_condition">
					<div class="row">
						<div class="col-xs-8 col-sm-9 col-md-10 col-lg-2">
							<spring:theme code="text.account.invoice.termsOfPayment" />
						</div>
						<div class="col-xs-4 col-sm-3 col-md-2 col-lg-10">${invoiceData.termsOfPayment}</div>
						<div class="col-xs-8 col-sm-9 col-md-10 col-lg-2">
							<spring:theme code="text.account.invoice.termsOfDelivery" />
						</div>
						<div class="col-xs-4 col-sm-3 col-md-2 col-lg-10">${invoiceData.termsOfDelivery}</div>
					</div>
				</div>
			</div>
		</div>
		<div class="well-content">
			<div class="col-sm-12 invoice-other-information">
				<div class="label-order">
					<spring:theme code="text.account.invoice.order.weight" />
				</div>
				<div class="invoice-terms_condition">
					<div class="row">
						<div class="col-xs-8 col-sm-9 col-md-10 col-lg-2">
							<spring:theme code="text.account.invoice.order.netWeight" />
						</div>
						<div class="col-xs-4 col-sm-3 col-md-2 col-lg-10">${invoiceData.netWeight}</div>
						<div class="col-xs-8 col-sm-9 col-md-10 col-lg-2">
							<spring:theme code="text.account.invoice.order.grossWeight" />
						</div>
						<div class="col-xs-4 col-sm-3 col-md-2 col-lg-10">${invoiceData.grossWeight}</div>
					</div>
				</div>
			</div>
		</div>
	</ycommerce:testId>
</div>
