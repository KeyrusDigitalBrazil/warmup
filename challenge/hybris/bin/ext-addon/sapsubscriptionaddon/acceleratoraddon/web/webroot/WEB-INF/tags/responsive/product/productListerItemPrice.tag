<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<ycommerce:testId code="searchPage_price_label_${product.code}">

		<%-- if product is multidimensional with different prices, show range, else, show unique price --%>
		<c:choose>
			<c:when test="${product.multidimensional and (product.priceRange.minPrice.value ne product.priceRange.maxPrice.value)}">
				<format:price priceData="${product.priceRange.minPrice}"/> - <format:price priceData="${product.priceRange.maxPrice}"/>
			</c:when>
			<c:otherwise>
			<c:choose>
				<c:when test="${not empty product.subscriptionCode}">
					<c:forEach items="${product.price.recurringChargeEntries}" var="recurringChargeEntry">
						<p class="price">
							<format:fromPrice priceData="${recurringChargeEntry.price}"/>
						</p>
						<c:if test="${product.subscriptionTerm.billingPlan ne null and product.subscriptionTerm.billingPlan.billingTime ne null}">
							<div class="item__code">${product.subscriptionTerm.billingPlan.billingTime.name}</div>
						</c:if>	
	                 </c:forEach>
	                <c:forEach items="${product.price.oneTimeChargeEntries}" var="oneTimeChargeEntry">
						<c:if test="${oneTimeChargeEntry.billingTime ne null and oneTimeChargeEntry.billingTime.code eq 'paynow'}">
						<p class="price">
							<format:fromPrice priceData="${oneTimeChargeEntry.price}"/>
						</p>
						<div class="item__code">${oneTimeChargeEntry.billingTime.name}</div>	
						</c:if>		
	                </c:forEach>             
				</c:when>
				<c:otherwise>
					<format:price priceData="${product.price}"/>
				</c:otherwise>	
			</c:choose>
			</c:otherwise>
		</c:choose>

</ycommerce:testId>
