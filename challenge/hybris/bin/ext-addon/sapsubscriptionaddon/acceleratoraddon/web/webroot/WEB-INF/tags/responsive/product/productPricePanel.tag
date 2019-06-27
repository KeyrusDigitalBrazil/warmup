<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="isOrderForm" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${empty product.volumePrices}">
		<c:choose>
			<c:when test="${(not empty product.priceRange) and (product.priceRange.minPrice.value ne product.priceRange.maxPrice.value) and ((empty product.baseProduct) or (not empty isOrderForm and isOrderForm))}">
				<span>
					<format:price priceData="${product.priceRange.minPrice}"/>
				</span>
				<span>
					-
				</span>
				<span>
					<format:price priceData="${product.priceRange.maxPrice}"/>
				</span>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${not empty product.subscriptionTerm}">
						<c:forEach items="${product.price.recurringChargeEntries}" var="recurringChargeEntry">
							<p class="price">
								<format:fromPrice priceData="${recurringChargeEntry.price}"/>
							</p>
							<div class="item__code">${product.subscriptionTerm.billingPlan.billingTime.name}</div>
						
                        </c:forEach>
                        <c:forEach items="${product.price.oneTimeChargeEntries}" var="oneTimeChargeEntry">
						<c:if test="${oneTimeChargeEntry.billingTime ne null and oneTimeChargeEntry.billingTime.code eq 'paynow'}">
							<p class="price">
								<format:fromPrice priceData="${oneTimeChargeEntry.price}"/>
							</p>
							<div class="item__code">${oneTimeChargeEntry.billingTime.name}</div>
						</c:if>				
                        </c:forEach>
                        <br><br><br>
                         
                        <%--Contract Term--%>
                        <c:if test="${not empty product.contractTerm}">
                        <div class="tr">
                                <div class="td">
                                     <spring:theme code="text.product.contractTerm"/>: ${product.contractTerm} &nbsp;<spring:theme code="text.product.usage.months"/>
                                 </div>
                        </div>
                        </c:if>
                        <br>
                        <div class="table">
                        	<div class="tr">
                        		<c:if test="${not empty product.price.usageCharges}">	
                        		<div class="td">
                       				<strong> <spring:theme code="text.product.usage.charges"/></strong>
                       			</div>
                       			</c:if>
                       		</div>
                       	<c:forEach items="${product.price.usageCharges}" var="usageCharge">
                            <c:choose>
	                            <%--Begin Block pricing --%>
	                        	<c:when test="${usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and (usageCharge.usageChargeType.code == 'block_usage_charge' or usageCharge.usageChargeType.code == '')}">
			                        	<c:if test="${usageCharge.includedQty > 0}">
				                        	<div class="tr">
				                        		<div class="td">
				                        			<spring:theme code="text.product.usage.includedQuantity"/> :
				                        		</div>
				                        		<div class="td">
				                        				 ${usageCharge.includedQty} &nbsp; ${usageCharge.usageUnit.namePlural}
				                        		</div>	
				                        	</div>
			                        	</c:if>
			                        	<c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry">	
			                        		<c:choose>
				                        		<c:when test ="${usageCharge.blockSize == 1}">
					                        		<div class="tr">
					                        			<div class="td">
					                        				<format:fromPrice priceData="${usageChargeEntry.price}"/> <spring:theme code="text.product.usage.per"/> ${usageCharge.usageUnit.name}			<br>								
														</div>
													</div>
												</c:when>
												<c:otherwise>
													<div class="tr">
					                        			<div class="td">
															<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp;
														</div>
														<div class="td">
															<spring:theme code="text.product.usage.every"/>&nbsp;${usageCharge.blockSize}&nbsp;${usageCharge.usageUnit.namePlural}		<br>									
														</div>
													</div>
												</c:otherwise>	
											</c:choose>
										</c:forEach>
									
									<br>
								</c:when>
								<%--End Block pricing --%>
								
								<%--Begin Tiered pricing --%>
								<c:when test="${usageCharge['class'].simpleName eq 'PerUnitUsageChargeData' and usageCharge.usageChargeType.code == 'each_respective_tier'}">
									<c:if test="${usageCharge.includedQty > 0}">
										<div class="tr">
						                      <div class="td">
			                        				<spring:theme code="text.product.usage.includedQuantity"/> ${usageCharge.includedQty} ${usageCharge.usageUnit.namePlural} <br>
			                        		  </div>
			                        	</div>
		                        	</c:if>
									<c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry">
										<c:if test="${usageChargeEntry['class'].simpleName eq 'TierUsageChargeEntryData'}">
											<c:set var="lastTierValue" value="${usageChargeEntry.tierEnd}"/>
											<div class="tr">
						                      <div class="td">
													<spring:theme code="text.product.usage.tier" 
													arguments="${usageChargeEntry.tierStart} ${usageCharge.usageUnit.name}, ${usageChargeEntry.tierEnd} ${usageCharge.usageUnit.name}"/>
													:
											   </div>
											
												<c:choose>
					                        		<c:when test ="${usageCharge.blockSize == 1}">	
					                        			<div class="td">
															<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp; <spring:theme code="text.product.usage.per"/> &nbsp;${usageCharge.usageUnit.name}<br> 
														</div>
													</c:when>
													<c:otherwise>
														<div class="td">
															<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp; <spring:theme code="text.product.usage.every"/> ${usageCharge.blockSize} &nbsp; ${usageCharge.usageUnit.namePlural}<br>
														</div>
													</c:otherwise>
												</c:choose>
											</div>
		                        		</c:if>
		                        		<c:if test="${usageChargeEntry['class'].simpleName eq 'OverageUsageChargeEntryData'}">	
		                        			<div class="tr">	
		                        				<div class="td">
				                        			<spring:theme code="text.product.usage.tier" arguments="${lastTierValue + 1} ${usageCharge.usageUnit.name}, Unlimited"/> : 
				                        		</div>
		                        			
			                        			<c:choose>
					                        		<c:when test ="${usageCharge.blockSize == 1}">	
					                        			<div class="td">
															<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp;	
															 <spring:theme code="text.product.usage.per"/> &nbsp;${usageCharge.usageUnit.name}<br> 
														</div>
													</c:when>
													<c:otherwise>
														<div class="td">
															<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp;	
															<spring:theme code="text.product.usage.every"/>&nbsp; ${usageCharge.blockSize} ${usageCharge.usageUnit.namePlural}<br>
														</div>
													</c:otherwise>
												</c:choose>
											</div>
		                        		</c:if>		                        		
		                        	</c:forEach>	
		                        	&nbsp;*<spring:theme code="text.product.usage.tierMessage"/>
		                        	<br>	<br>					
								</c:when>
								<%--End Tiered pricing --%>
								
								<%--Begin Volume Pricing --%>
								<c:when test="${usageCharge['class'].simpleName eq 'VolumeUsageChargeData'}">
									<c:forEach items="${usageCharge.usageChargeEntries}" var="usageChargeEntry">
									<div class="tr">
										<c:if test="${usageChargeEntry['class'].simpleName eq 'TierUsageChargeEntryData'}">
											<c:set var="lastTierValue" value="${usageChargeEntry.tierEnd}"/>
												
						                     		 <div class="td">
														<spring:theme code="text.product.usage.tier" arguments="${usageChargeEntry.tierStart} ${usageCharge.usageUnit.name}, 
														${usageChargeEntry.tierEnd} ${usageCharge.usageUnit.name}"/> : 
													 </div>
												
													<c:if test="${usageChargeEntry.fixedPrice.value > 0}">
														<div class="td">
															<format:fromPrice priceData="${usageChargeEntry.fixedPrice}"/> &nbsp; <spring:theme code="text.product.usage.plus"/> &nbsp;
														</div>
													</c:if>
													<div class="td">
														<format:fromPrice priceData="${usageChargeEntry.price}"/> &nbsp; <spring:theme code="text.product.usage.per"/>&nbsp; ${usageCharge.usageUnit.name}<br> 
				                        			</div>
		                        		</c:if>
		                        		<c:if test="${usageChargeEntry['class'].simpleName eq 'OverageUsageChargeEntryData'}">			
		                        			<div class="td">
		                        				<spring:theme code="text.product.usage.tier" arguments="${lastTierValue + 1} ${usageCharge.usageUnit.name}, Unlimited"/> : 
		                        			</div>
		                        			<c:if test="${usageChargeEntry.fixedPrice.value > 0}">
		                        				<div class="td">
													<format:fromPrice priceData="${usageChargeEntry.fixedPrice}"/> <spring:theme code="text.product.usage.plus"/>
												</div>
											</c:if>
											<div class="td">
		                        				<format:fromPrice priceData="${usageChargeEntry.price}"/>&nbsp;<spring:theme code="text.product.usage.per"/>&nbsp;${usageCharge.usageUnit.name}<br> 
		                        			</div>
		                        		</c:if>	
		                        		</div>	                        		
		                        	</c:forEach>	
		                        	&nbsp;*<spring:theme code="text.product.usage.volumeMessage"/>
								
								</c:when>
								<%--End Volume Pricing --%>
								
							</c:choose>
                        </c:forEach>
                       </div>
                    </c:when>
					<c:otherwise>
						<p class="price">
							<format:fromPrice priceData="${product.price}" />
						</p>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<table class="volume__prices" cellpadding="0" cellspacing="0" border="0">
			<thead>
			<th class="volume__prices-quantity"><spring:theme code="product.volumePrices.column.qa"/></th>
			<th class="volume__price-amount"><spring:theme code="product.volumePrices.column.price"/></th>
			</thead>
			<tbody>
			<c:forEach var="volPrice" items="${product.volumePrices}">
				<tr>
					<td class="volume__price-quantity">
						<c:choose>
							<c:when test="${empty volPrice.maxQuantity}">
								${volPrice.minQuantity}+
							</c:when>
							<c:otherwise>
								${volPrice.minQuantity}-${volPrice.maxQuantity}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="volume__price-amount text-right">${fn:escapeXml(volPrice.formattedValue)}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>
