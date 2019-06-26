<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<spring:htmlEscape defaultHtmlEscape="true" />		

<c:url value="/my-account/subscription/bills/" var="viewSubscriptionBillsUrl"/>

	<!-- Display Bills -->
	<div class="account-section-content	">
		<div class="account-orderhistory">
			<div class="account-overview-table">
				<table class="orderhistory-list-table responsive-table">
<form:form id="billsForm" name="subscriptionBillForm" commandName="subscriptionBillForm" action="${fn:escapeXml(viewSubscriptionBillsUrl)}" method="post" autocomplete="off">

						<tr class="account-orderhistory-table-head responsive-table-head hidden-xs"  style="text-align:left">
							<th style="text-align:left"><spring:theme code="Bills" text="Bills"/></th>
						</tr>
						<tr class="responsive-table-item">
						<td>
							<div class="item__code" style="text-align: left">
								<spring:theme code="text.account.subscription.bill.fromDate" text="From"/> &nbsp;
								<input id="fromDate" name="fromDate" placeholder=" Date is inclusive" type="text"/>	&nbsp;&nbsp;&nbsp;&nbsp;
								<spring:theme code="text.account.subscription.bill.tillDate" text="To"/>&nbsp; 
								<input id="toDate" name="toDate" placeholder=" Date is exclusive"  type="text" />&nbsp;&nbsp;
								<button id="viewBills" type="submit" class="btn btn-default btn-block" 
										style="display: inline-block; margin: 10px; height: 45px; width: 120px;">
									<spring:theme code="text.account.subscriptions.viewbills" text="View Bills" />
								</button>
							</div>
						</td>
						</tr>
				
</form:form>
</table>
			</div>
		</div>
	</div>
<br><br>

<c:if test="${empty subscriptionBills}">
	<div class="account-section-content content-empty">
		<spring:theme code="text.account.subscriptions.nobills" text="You have no bills"/>
	</div>
</c:if>

<c:if test="${not empty subscriptionBills}">
	<div class="account-section-content	">
		<div class="account-orderhistory">
            <div class="account-overview-table">
				<table class="orderhistory-list-table responsive-table">
					<tr class="account-orderhistory-table-head responsive-table-head hidden-xs">
						<th><spring:theme code="text.account.subscriptions.bill.id" text="Bill Id"/></th>
						<th><spring:theme code="Btext.account.subscriptions.bill.date" text="Billing Date"/></th>
	                    <th><spring:theme code="text.account.subscriptions" text="Subscriptions"/></th>
	                    <th><spring:theme code="text.account.subscription.bill.totalAmount" text="Total Amount"/></th>
	                    <th><spring:theme code="text.account.subscription.actions" text="Actions"/></th>
					</tr>
					<c:forEach items="${subscriptionBills}" var="subscriptionBill">              
						<tr class="responsive-table-item">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.documentNumber"/></td>
								<td class="responsive-table-cell">
                                		${subscriptionBill.billingId}
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.productName" text="Product Name"/></td>
								<td class="responsive-table-cell">
								<fmt:formatDate value="${subscriptionBill.subscriptionBillDate}" dateStyle="long" timeStyle="short" type="date"/>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.startDate" text="Start Date"/></td>													
								<td class="responsive-table-cell">${subscriptionBill.items}</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.endDate" text="End Date"/></td>
								<td class="responsive-table-cell"><format:price priceData="${subscriptionBill.price}"></format:price></td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.actions" text="Actions"/></td>
								<td class="responsive-table-cell">
									<a href="${viewSubscriptionBillsUrl}${subscriptionBill.billingId}" class="responsive-table-link">
										<spring:theme code="text.manage" text="View Bill"/>
									</a>
								</td>								
						</tr>
					</c:forEach>
				</table>
            </div>
		</div>		
	</div>
</c:if>



