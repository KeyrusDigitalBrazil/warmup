<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="componentId" required="true" type="java.lang.String" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="productaddon" tagdir="/WEB-INF/tags/addons/sapsubscriptionaddon/responsive/product" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>


<spring:htmlEscape defaultHtmlEscape="true" />

<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>

<c:set value="${not empty product.potentialPromotions}" var="hasPromotion"/>

<li class="product__list--item">
	<ycommerce:testId code="test_searchPage_wholeProduct">
		<a class="product__list--thumb" href="${productUrl}" title="${fn:escapeXml(product.name)}" >
			<product:productPrimaryImage product="${product}" format="thumbnail"/>
		</a>
		<ycommerce:testId code="searchPage_productName_link_${product.code}">
			<a class="product__list--name" href="${productUrl}">${ycommerce:sanitizeHTML(product.name)}</a>
		</ycommerce:testId>

		<div class="product__list--price-panel">
			<c:if test="${not empty product.potentialPromotions}">
				<div class="product__listing--promo">
					<c:forEach items="${product.potentialPromotions}" var="promotion">
						${ycommerce:sanitizeHTML(promotion.description)}
					</c:forEach>
				</div>
			</c:if>

			<ycommerce:testId code="searchPage_price_label_${product.code}">
				<div class="product__listing--price"><productaddon:productListerItemPrice product="${product}"/></div>
			</ycommerce:testId>
		</div>

		<c:if test="${not empty product.summary}">
			<div class="product__listing--description">${ycommerce:sanitizeHTML(product.summary)}</div>
		</c:if>



		<c:set var="product" value="${product}" scope="request"/>
		<c:set var="addToCartText" value="${addToCartText}" scope="request"/>
		<c:set var="addToCartUrl" value="${addToCartUrl}" scope="request"/>
		<div class="addtocart">
			<div id="actions-container-for-${componentId}" class="row">
				<c:forEach items="${actions}" var="action" varStatus="idx">
					<c:if test="${action.visible}">
						<div class="${componentId}-${fn:escapeXml(action.uid)}" data-index="${idx.index + 1}" class="${styleClass}">
							<cms:component component="${action}" parentComponent="${component}" evaluateRestriction="true"/>
						</div>
					</c:if>
				</c:forEach>
			</div>
		</div>
	</ycommerce:testId>
</li>