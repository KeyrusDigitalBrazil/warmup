<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>

<spring:url value="/my-account/subscription/bills" var="subscriptionBillsUrl" />


<common:headline url="${subscriptionBillsUrl}" labelKey="text.account.subscription.bill.details"/>


<c:if test="${empty subscriptionBill}">
	<div class="account-section-content content-empty">
		<spring:theme code="text.account.subscriptions.noSubscriptionBills"
			text="You have No bills" />
	</div>
</c:if>

<c:if test="${not empty subscriptionBill}">
	<c:forEach items="${subscriptionBill}" var="bill">
	
	<c:url value="${bill.productUrl}" var="productUrl"/>
	
		<div class="well well-quinary well-xs">

			<div class="well-headline">
				<spring:theme code="text.account.subscription.documentNumber"/>&nbsp;&nbsp; ${bill.subscriptionId}
			</div>

			<div class="well-content">
				<div class="row">
					<div class="col-sm-12 col-md-9">
						<div class="row">

							<!-- Column 1 -->

							<div class="col-sm-6 col-md-4 order-billing-address">
							<div class="table">	
								<div class="order-ship-to"
									style="text-transform: uppercase; font-weight: bold">	
									<div class="label-order">
										<div class="tr">
											<div class="td">
												<spring:theme code="text.account.subscriptions.bill.data"/>
											</div>
										</div>
									</div>
								</div>	
																														
									<div class="value-order">
										<div class="tr">
											<div class="td">
												<spring:theme code="text.account.subscription.productName"/> :
											</div>
											<div class="td">
												<a href="${productUrl}">${bill.productCode}</a>
											</div>
										</div>
									</div>
									<div class="value-order">
										<div class="tr">
											<div class="td">
												<spring:theme code="text.account.subscription.bill.totalAmount"/> :
											</div>
											<div class="td">
												<format:price priceData="${bill.price}"></format:price>
											</div>
										</div>
									</div>
								</div>
							</div>

							<!-- Column 2 -->
							
						
							<div class="col-sm-6 col-md-4 order-billing-address">
							
								<c:forEach items="${bill.charges}" var="charge">
									<div class="table">
	
											<div class="order-ship-to"
												style="text-transform: uppercase; font-weight: bold">
	
												<div class="label-order">
													<div class="tr">
														<div class="td">
															<c:if test="${not empty charge.name}">
																${charge.name}
															</c:if>&nbsp;
														<spring:theme
																code="text.account.subscriptions.bill.charges"/>
														</div>
													</div>
												</div>
											</div>
	
											<div class="value-order">
												<div class="tr">
													<div class="td">
														<spring:theme
															code="text.account.subscriptions.bill.netAmount" /> :
													</div>
													<div class="td">
														<format:price priceData="${charge.netAmount}"></format:price>
													</div>
												</div>
											</div>
	
											<div class="value-order">
												<div class="tr">
													<div class="td">
														<spring:theme
															code="text.account.subscriptions.bill.ratingPeriod" /> :
													</div>
													<div class="td">
														<fmt:formatDate value="${charge.fromDate}" dateStyle="long" timeStyle="short" type="date" />
														<c:if test="${charge.toDate!=null}">
															&nbsp;<spring:theme code="text.account.subscription.bill.tillDate" />&nbsp;
															<fmt:formatDate value="${charge.toDate}" dateStyle="long" timeStyle="short" type="date" />
														</c:if>
													</div>
												</div>
											</div>
											<c:if test="${charge.usage!=null}">
												<div class="value-order">
													<div class="tr">
														<div class="td">
															<spring:theme code="text.account.subscription.bill.usage" /> :
														</div>
														<div class="td">
															${charge.usage}&nbsp;${charge.usageUnit.id}</div>
													</div>
												</div>
											</c:if>
										</div>
									<br>
										
									</c:forEach>
									
							
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</c:forEach>
</c:if>
<br>
<br>
<br>
<br>