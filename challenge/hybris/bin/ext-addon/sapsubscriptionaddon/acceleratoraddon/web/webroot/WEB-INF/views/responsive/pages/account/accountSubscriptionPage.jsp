<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="productaddon"
	tagdir="/WEB-INF/tags/addons/sapsubscriptionaddon/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<head>
	<link href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css" rel="stylesheet" type="text/css"/>
</head>
<spring:htmlEscape defaultHtmlEscape="true"/>
<c:url value="${subscriptionData.productUrl}" var="productUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/cancel" var="cancelSubscriptionUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/extend" var="extendSubscriptionUrl"/>
<spring:url value="/my-account/subscriptions" var="subscriptionUrl" />

<c:url value="/my-account/subscription/${subscriptionData.id}/caleffDate/${subscriptionData.version}" var="calcEffCanellationDateUrl"/>

<common:headline url="${subscriptionUrl}" labelKey="text.account.subscription.detail"/>

<form:form id="subscriptionCancellationForm" name="subscriptionCancellationForm" action="${fn:escapeXml(cancelSubscriptionUrl)}" method="post" commandName="subscriptionCancellationForm">
	<div class="account-section-content	">
		<div class="account-orderhistory">
			<div class="account-overview-table">
				<table class="orderhistory-list-table responsive-table">
					<tr	class="account-orderhistory-table-head responsive-table-head hidden-xs">
						<th><spring:theme code="text.account.subscription.productName" text="Product Name"/></th>
						<th><spring:theme code="text.account.subscription.price" text="Price"/></th>
						<th><spring:theme code="text.account.subscription.status" text="Status"/></th>
						<th><spring:theme code="text.account.subscription.startDate" text="Start Date"/></th>
						<th><spring:theme code="text.account.subscription.endDate" text="End Date"/></th>
						<th></th>
					</tr>
					<tr class="responsive-table-item">
						<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.productName" text="Product Name"/></td>
						<td class="responsive-table-cell"><a href="${productUrl}">${subscriptionData.name}</a></td>
						<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.price" text="Price"/></td>
						<td class="responsive-table-cell" style="width:1px;white-space:no-wrap;">
						<div id="table">
								<c:forEach items="${subscriptionData.pricePlan.recurringChargeEntries}"	var="recurringChargeEntry">
									<div class="tr">
										<div class="td"><format:fromPrice priceData="${recurringChargeEntry.price}"/></div>
									</div>
										<div class="tr">${subscriptionData.billingFrequency}</div>
								</c:forEach><br>								
								<c:forEach items="${subscriptionData.pricePlan.oneTimeChargeEntries}" var="oneTimeChargeEntry">
										<c:if test="${oneTimeChargeEntry.billingTime.code eq 'paynow'}">
											 <div class="tr">
											 	<div class="td">
											 		<format:fromPrice priceData="${oneTimeChargeEntry.price}"/>
											 	</div>	
											 </div>						
										    <div class="tr">
										    	<div class="td">
										   				 ${oneTimeChargeEntry.billingTime.name}
										   		</div>
										   	</div>
									    </c:if>
								</c:forEach>
								<br><br>
								
								
								
								<c:if test="${subscriptionData.pricePlan ne null and not empty subscriptionData.pricePlan.usageCharges}">
									<div class="tr">
										<div class="td">
											<strong><u><spring:theme code="text.product.usage.charges" text="Usage Charges"/></u> :</strong>
										</div>
									</div>
									<br>
									
								<c:forEach var="usageCharge" items="${subscriptionData.pricePlan.usageCharges}">
									<c:choose> 
										<c:when test="${(usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and usageCharge.usageChargeType.code == 'block_usage_charge')}">
											<c:if test="${usageCharge.includedQty > 0}">
												<div class="tr">
													<div class="td"><spring:theme code="text.product.usagecharge.blockprice.incluedQty" text="Included-Quantity"/></div>
													<div class="td">${usageCharge.includedQty} &nbsp;${usageCharge.usageUnit.namePlural}</div>
												</div>
												<div class="tr">
													<c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry">
															<div class="td"> ${usageChargeEntry.price.formattedValue}</div>
															<div class="td">
																<spring:theme code="text.product.usage.every" text="every"/>&nbsp;${usageCharge.blockSize}&nbsp;${usageCharge.usageUnit.namePlural}
															</div>
													</c:forEach>
												</div>
											</c:if>
											<br>
										</c:when>
									
										<c:when test="${(usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and usageCharge.usageChargeType.code == 'each_respective_tier')}">
												<div class="tr">
													<div class="td">
														<strong><spring:theme code="text.product.usagecharge.charges.tierprice" text="Tier Pricing"/></strong>
													</div>
												</div>
												<c:if test="${usageCharge.includedQty > 0}">
													<div class="tr">
														<div class="td"><spring:theme code="text.product.usagecharge.blockprice.incluedQty" text="Included-Quantity"/></div>
														<div class="td">${usageCharge.includedQty}&nbsp;${usageCharge.usageUnit.name}</div>
													</div>
												</c:if>
											
												<c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry">
													<c:if test="${usageChargeEntry['class'].simpleName eq 'TierUsageChargeEntryData'}">
													<c:set var="lastTierValue" value="${usageChargeEntry.tierEnd}"/>
													<div class="tr">
														<div class="td">
															<spring:theme code="text.product.usage.tier" arguments="${usageChargeEntry.tierStart} ${usageCharge.usageUnit.name}, ${usageChargeEntry.tierEnd} ${usageCharge.usageUnit.name}"/>
														</div>
														 <div class="td">
														   <c:choose>
								                        		<c:when test ="${usageCharge.blockSize == 1}">	
																	<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp; <spring:theme code="text.product.usage.per"/>&nbsp;${usageCharge.usageUnit.name}<br> 
																</c:when>
																<c:otherwise>
																	<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp; <spring:theme code="text.product.usage.every"/>&nbsp;${usageCharge.blockSize} ${usageCharge.usageUnit.namePlural}<br>
																</c:otherwise>
															</c:choose>
														 </div>
													</div>
													</c:if>
														
													<c:if test="${usageChargeEntry['class'].simpleName eq 'OverageUsageChargeEntryData'}">
														<div class="tr">
															<div class="td">
																<spring:theme code="text.product.usage.tier" arguments="${lastTierValue + 1} ${usageCharge.usageUnit.name}, Unlimited"/>
															</div>
															<div class="td">
																<c:choose>
									                        		<c:when test ="${usageCharge.blockSize == 1}">	
																		<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp; <spring:theme code="text.product.usage.per"/> &nbsp;${usageCharge.usageUnit.name}<br> 
																	</c:when>
																	<c:otherwise>
																		<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp; <spring:theme code="text.product.usage.every"/>&nbsp; ${usageCharge.blockSize} ${usageCharge.usageUnit.namePlural}<br>
																	</c:otherwise>
																</c:choose>
															</div>
														</div>
													</c:if>
												</c:forEach>
												<br>
										</c:when>
										
										<c:when test="${usageCharge['class'].simpleName eq 'VolumeUsageChargeData'}">
										<div class="tr">
											<div class="td">
												<strong><spring:theme code="text.product.usagecharge.volumeprice" text="Volume Pricing"/></strong>
											</div>
										</div>
										
												<c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry">
														<c:if test="${usageChargeEntry['class'].simpleName eq 'TierUsageChargeEntryData'}">
															<c:set var="lastTierValue" value="${usageChargeEntry.tierEnd}"/>
															<div class="tr">	
																<div class="td">
																<spring:theme code="text.product.usage.tier" arguments="${usageChargeEntry.tierStart} ${usageCharge.usageUnit.name}, ${usageChargeEntry.tierEnd} ${usageCharge.usageUnit.name}"/>
																 </div>
																<%-- <div class="td">	 
																	<spring:theme code="text.product.usagecharge.blockprice.priceperblock" text="Price Per block"/>
																</div> --%>
																<div class="td">	
																	  ${usageChargeEntry.price.formattedValue}&nbsp;
																	<spring:theme code="text.product.usage.per" text="per"/>&nbsp;${usageCharge.usageUnit.name}
																</div>
														</div>
														</c:if>
															
														<c:if test="${usageChargeEntry['class'].simpleName eq 'OverageUsageChargeEntryData'}">
														<div class="tr">	
															<div class="td">
																<spring:theme code="text.product.usage.tier" arguments="${lastTierValue + 1} ${usageCharge.usageUnit.name}, Unlimited"/>
															</div>
															<%-- <div class="td">
																<spring:theme code="text.product.usagecharge.blockprice.priceperblock" text="Price Per block"/>
															</div>	 --%>
															<div class="td">
																${usageChargeEntry.price.formattedValue}&nbsp;
																	<spring:theme code="text.product.usage.per" text="per"/>&nbsp;${usageCharge.usageUnit.name}<br>
															</div>
														</div>
														
														</c:if>
													
												</c:forEach>
											
										</c:when>	
									</c:choose>	
								 </c:forEach>
							</c:if>
						</div>
						</td>
						<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.status" text="Status"/></td>
						<td class="responsive-table-cell"><spring:theme code="text.account.subscriptions.status.${subscriptionData.status}" /></td>
						<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.startDate" text="Start Date"/></td>
						<td class="responsive-table-cell"><fmt:formatDate value="${subscriptionData.startDate}" dateStyle="long" timeStyle="short" type="date"/></td>
						<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.subscription.endDate" text="End Date"/></td>
						<td class="responsive-table-cell">
							<c:if test="${not empty subscriptionData.endDate}">
								<fmt:formatDate	value="${subscriptionData.endDate}" dateStyle="long" timeStyle="short" type="date" />
							</c:if>
						</td>
						<c:set var="rcSubStatus" value="${subscriptionData.status}" />
						<td class="hidden-sm hidden-md hidden-lg"></td>
						<td>
							<c:if test="${subscriptionData.status eq 'ACTIVE'}">
								<button id="cancelRCSubscription" type="submit" class="btn btn-primary btn-block" data-cancel-sub-url="${calcEffCanellationDateUrl}">
									<spring:theme code="text.account.subscription.cancelSubscription" text="Cancel"/>
								</button>
							</c:if>
							<input type="hidden" name="version"	value="${fn:escapeXml(subscriptionData.version)}"/>
							<input type="hidden" name="ratePlanId" value="${fn:escapeXml(subscriptionData.ratePlanId)}"/>
							<c:if test="${not empty subscriptionData.validTillDate}">
								 <input	type="hidden" name="subscriptionEndDate" value="${fn:escapeXml(subscriptionData.validTillDate)}" />
							</c:if>
						</td>
					</tr>
					
				</table></div></div></div><br><br>
				

			<div class="account-section-content">
				<div class="account-orderhistory">
					<div class="account-overview-table">
						<table class="orderhistory-list-table responsive-table">
							<tr class="account-orderhistory-table-head responsive-table-head hidden-xs" style="text-align: left">
								<th style="text-align: left"><spring:theme code="Current Usage" text="Current Usage" /></th>
								<th></th>
							</tr>
							
									<tr class="responsive-table-item" style="text-align: left">
										<td class="responsive-table-cell" style="text-align: left">
										<div class="table">
											<c:forEach items="${subscriptionData.currentUsages}" var="charge">
													<c:if test="${charge.usage!=null}">
												
													<div class="tr">
														<div class="td">
															&nbsp;${charge.usage}&nbsp;${charge.usageUnit.id}
														</div>
														<div class="td">&nbsp;</div>
														<div class="td">
															<spring:theme code="text.account.subscriptions.bill.netAmount" /> : &nbsp;<format:fromPrice priceData="${charge.netAmount}"/>
														</div>
													</div>
												
												
								</c:if>
							</c:forEach></div>
										</td><td></td>
									</tr>
								
						</table>
					</div>
				</div>
			</div>
		
</form:form>
		
<br><br>

<c:if test="${subscriptionData.status eq 'ACTIVE' && not empty subscriptionData.validTillDate}">
					
						<form:form id="subscriptionExtensionForm" name="subscriptionExtensionForm" commandName="subscriptionExtensionForm" action="${fn:escapeXml(extendSubscriptionUrl)}" method="post">
							<div class="form" id="extendRCSubscription">
								<div class="form-group" style="display: inline-block; margin: 10px; width: 180px;">Extend Subscription By</div>
								<div class="form-group" style="display: inline-block; margin: 10px; width: 300px;">
									<input type="text" placeholder="enter valid number" name="extensionPeriod" id="extensionPeriod" class="form-control"/>
								</div>	
								<div class="form-group" style="display: inline-block; margin: 10px; width: 100px;">${fn:escapeXml(subscriptionData.contractFrequency)}</div>						
								<div class="form-group" style="display: inline-block; margin: 10px; width: 300px;">
									<button id="extendSubscription" type="submit"
										class="btn btn-primary btn-block"
										class="btn btn-primary btn-block">
										<spring:theme code="text.account.subscription.extend" text="Extend Subscription" />
									</button>
									<input type="hidden" name="version"	value="${fn:escapeXml(subscriptionData.version)}"/>
									<input type="hidden" name="ratePlanId" value="${fn:escapeXml(subscriptionData.ratePlanId)}"/>
									<input type="hidden" name="billingFrequency" value="${fn:escapeXml(subscriptionData.contractFrequency)}"/>
									<c:if test="${not empty subscriptionData.validTillDate}">
											<input type="hidden" name="validTilldate" value="${fn:escapeXml(subscriptionData.validTillDate)}" />
									</c:if>
								</div>
								<div class="form-group">
									<input id="rcSubUnlimited" name="unlimited" type="checkbox">
									<label class="control-label notification_preference_channel">
			   							<span>Select for Unlimited Subscription</span>	
				   					</label></div>
							</div>
						</form:form>
				</c:if>
