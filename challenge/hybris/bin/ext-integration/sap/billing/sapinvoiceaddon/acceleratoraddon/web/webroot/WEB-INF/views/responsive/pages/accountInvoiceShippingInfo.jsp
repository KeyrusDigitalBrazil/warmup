<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="invoice"
	tagdir="/WEB-INF/tags/addons/sapinvoiceaddon/responsive/invoice"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<spring:htmlEscape defaultHtmlEscape="true" />

<div class="account-orderdetail well well-tertiary">
	<ycommerce:testId code="orderDetails_paymentDetails_section">
		<div class="well-content">
				<c:if test="${invoiceData.partnerbBillToAddress ne null}">
				<div class="col-sm-6 order-billing-address">
					<div class="label-order">
						<spring:theme code="text.account.invoice.billToAddress" />
					</div>
					<div class="value-order">${invoiceData.partnerbBillToAddress.partnerID}<br>${invoiceData.partnerbBillToAddress.streetHouseNumber1}<br>${invoiceData.partnerbBillToAddress.postalCode}<br>${invoiceData.partnerbBillToAddress.city}</div>
				</div>
			</c:if>	
			<c:if test="${invoiceData.partnerShipToAddress ne null}">
				<div class="col-sm-6 order-billing-address">
					<div class="label-order">
						<spring:theme code="text.account.invoice.shipToAddress" />
					</div>
					<div class="value-order">${invoiceData.partnerShipToAddress.partnerID}<br>${invoiceData.partnerShipToAddress.streetHouseNumber1}<br>${invoiceData.partnerShipToAddress.postalCode}<br>${invoiceData.partnerShipToAddress.city}</div>
				</div>
			</c:if>
		</div>
	</ycommerce:testId>
</div>

