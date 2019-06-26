<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="invoice"
	tagdir="/WEB-INF/tags/addons/sapinvoiceaddon/responsive/invoice"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="account-invoiceitemdetails-section-content">
	<div class="account-itemdetails">
		<div class="responsive-table">
			<table class="responsive-table">
				<thead>
					<tr class="responsive-table-head hidden-xs">
						<th id="header1"><spring:theme
								code="text.account.invoice.posno" /></th>
						<th id="header2"><spring:theme
								code="test.account.invoice.itemno" /></th>
						<th id="header3"><spring:theme
								code="text.account.invoice.itemDesc" /></th>
						<th id="header4"><spring:theme code="text.quantity" /></th>
						<th id="header5"><spring:theme
								code="text.account.invoice.grossPrice" /></th>
						<th id="header6"><spring:theme
								code="text.account.invoice.netPrice" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${invoiceData.invoiceItemsData.entries}"
						var="entry">
						<tr class="responsive-table-item">
							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="text.account.invoice.posno" /></td>
							<td headers="header1" class="responsive-table-cell">
								${fn:escapeXml(entry.posNo)}</td>
							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="test.account.invoice.itemno" /></td>

							<c:choose>
								<c:when test="${entry.product.url ne null}">
									<c:url value="${entry.product.url}" var="productUrl" />
									<td headers="header2" class="responsive-table-cell"><ycommerce:testId
											code="invoiceDetails_product_link">
											<div class="itemName">
												<a href="${entry.product.purchasable ? productUrl : ''}"
													class="responsive-table-link">${entry.product.code}</a>
											</div>
										</ycommerce:testId>
								</c:when>
								<c:otherwise>
									<td headers="header2" class="responsive-table-cell"><ycommerce:testId
											code="invoiceDetails_product_link">
											<div class="itemName">${entry.product.code}</div>
										</ycommerce:testId></td>
								</c:otherwise>
							</c:choose>

							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="text.account.invoice.itemDesc" /></td>
							<td headers="header3" class="responsive-table-cell">
								${fn:escapeXml(entry.itemDesc)}</td>
							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="text.account.invoice.itemDesc" /></td>
							<td headers="header4" class="responsive-table-cell">
								${fn:escapeXml(entry.quantity)}</td>
							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="text.account.invoice.grossPrice" /></td>
							<td headers="header5"
								class="responsive-table-cell responsive-table-cell-bold"><invoice:priceInvoice
									priceData="${entry.grossPrice}" displayFreeForZero="true" /></td>

							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="text.account.invoice.netPrice" /></td>
							<td headers="header6"
								class="responsive-table-cell responsive-table-cell-bold"><invoice:priceInvoice
									priceData="${entry.netPrice}" displayFreeForZero="true" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>


<div class="invoice-order-totals">
	<div class="row">
		<c:if test="${invoiceData.invoiceItemsData ne null}">
			<div class="col-xs-8 col-sm-9 col-md-10 col-lg-11">
				<spring:theme code="text.account.invoice.totalNet" />
			</div>
			<div class="col-xs-4 col-sm-3 col-md-2 col-lg-1">
				<ycommerce:testId code="Order_Totals_Subtotal">
					<invoice:priceInvoice
						priceData="${invoiceData.invoiceItemsData.netValue}"
						displayFreeForZero="true" />
				</ycommerce:testId>
			</div>


			<div class="col-xs-8 col-sm-9 col-md-10 col-lg-11">
				<spring:theme code="text.account.invoice.overallTax" />
			</div>
			<div class="col-xs-4 col-sm-3 col-md-2 col-lg-1">
				<ycommerce:testId code="cart_totalPrice_label">
					<invoice:priceInvoice
						priceData="${invoiceData.invoiceItemsData.overAllTax}"
						displayFreeForZero="true" />
				</ycommerce:testId>
			</div>

			<div class="col-xs-8 col-sm-9 col-md-10 col-lg-11 billed-value">
				<spring:theme code="text.account.invoice.grandTotal" />
			</div>
			<div class="col-xs-4 col-sm-3 col-md-2 col-lg-1 billed-value">
				<ycommerce:testId code="cart_totalPrice_label">
					<invoice:priceInvoice
						priceData="${invoiceData.invoiceItemsData.grandTotal}"
						displayFreeForZero="true" />
				</ycommerce:testId>
			</div>
		</c:if>
	</div>
</div>