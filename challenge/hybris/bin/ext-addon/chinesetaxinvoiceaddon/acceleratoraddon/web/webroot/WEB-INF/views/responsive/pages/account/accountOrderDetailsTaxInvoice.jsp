<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:choose>
	<c:when test="${not empty orderData.taxInvoice}">
		<div class="account-orderdetail well well-tertiary">
			<div class="well-headline">
				<spring:theme code="text.account.order.orderDetails.taxinvoice" />
			</div>
			<ycommerce:testId code="orderDetails_paymentDetails_section">
				<div class="well-content col-sm-12 col-md-9">
					<div class="row">
						<div class="col-sm-6 col-md-4 order-payment-data">
							<div class="label-order">
								<spring:theme code="invoice.category" />
							</div>
							<div id="taxinvoice-category" class="value-order">
								<spring:theme code="type.enum.invoice.category.${orderData.taxInvoice.category}.code" />
							</div>
						</div>
						<div class="col-sm-6 col-md-4 order-payment-data">
							<div class="label-order">
								<spring:theme code="invoice.recipientType" />
							</div>
							<div id="taxinvoice-recipientType" class="value-order">
								<spring:theme code="type.enum.invoice.recipientType.${orderData.taxInvoice.recipientType}.code" />
							</div>
						</div>
						<div class="col-sm-6 col-md-4 order-payment-data">
							<div class="label-order">
									<spring:theme code="invoice.recipient" />
							</div>
							<div id="taxinvoice-recipient" class="value-order">
								<spring:theme text="${orderData.taxInvoice.recipient}" />
							</div>
						</div>
					</div>
				</div>
			</ycommerce:testId>
		</div>
	</c:when>
	<c:otherwise>
		<div class="account-orderdetail well well-tertiary account-consignment">
			<div class="well-headline well-headline-taxinvoice">
				<spring:theme code="text.account.order.orderDetails.taxinvoice" />
				<span class="well-headline-sub"> <spring:theme
						code="text.account.order.orderDetails.taxinvoice.notRequired" />
				</span>
			</div>
		</div>
	</c:otherwise>
</c:choose>
