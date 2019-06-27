<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="tabhead of-carousel">
	<a href="">${fn:escapeXml(title)}</a> <span class="glyphicon"></span>
</div>
<div class="tabbody">
	<c:choose>
		<c:when test="${not empty productData}">
			<div class="carousel__component">
				<div class="carousel__component--headline">
					<c:choose>
						<c:when test="${empty landingPage}">${fn:escapeXml(title)}</c:when>
						<c:otherwise>
							<a href="${fn:escapeXml(landingPage)}/">${fn:escapeXml(title)}</a>
						</c:otherwise>
					</c:choose>
				</div>
	
				<c:choose>
					<c:when test="${component.popup}">
						<div class="carousel__component--carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
							<div id="quickViewTitle" class="quickView-header display-none">
								<div class="headline">
									<span class="headline-text"><spring:theme code="popup.quick.view.select"/></span>
								</div>
							</div>
							<c:forEach items="${productData}" var="product">
	
								<spring:url value="${product.url}/quickView" var="productQuickViewUrl" htmlEscape="false"/>
								<div class="carousel__item">
									<a href="${fn:escapeXml(productQuickViewUrl)}" class="js-reference-item">
										<div class="carousel__item--thumb">
											<product:productPrimaryReferenceImage product="${product}" format="product"/>
										</div>
										<div class="carousel__item--name">${fn:escapeXml(product.name)}</div>
										<div class="carousel__item--price"><format:fromPrice priceData="${product.price}"/></div>
									</a>
								</div>
							</c:forEach>
						</div>
					</c:when>
					<c:otherwise>
						<div class="carousel__component--carousel js-owl-carousel js-owl-default">
							
							<c:if test="${not empty vendorLogo}">
								<div class="carousel__item">
									<a href="${empty landingPage ? '#' : fn:escapeXml(landingPage)}">
										<div class="carousel__item--name">&nbsp;</div>
										<div class="carousel__item--thumb">
											<img width="284" height="284" src="${fn:escapeXml(vendorLogo.url)}"
												alt="${fn:escapeXml(vendorLogo.altText)}" title="${fn:escapeXml(vendorLogo.altText)}" >
										</div>
										<div class="carousel__item--price">&nbsp;</div>
									</a>
								</div>
							</c:if>
							
							<c:forEach items="${productData}" var="product">
	
								<spring:url value="${product.url}" var="productUrl" htmlEscape="false"/>
	
								<div class="carousel__item">
									<a href="${fn:escapeXml(productUrl)}">
										<div class="carousel__item--thumb">
											<product:productPrimaryImage product="${product}" format="product"/>
										</div>
										<div class="carousel__item--name">${fn:escapeXml(product.name)}</div>
										<div class="carousel__item--price"><format:fromPrice priceData="${product.price}"/></div>
									</a>
								</div>
							</c:forEach>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</c:when>
	
		<c:otherwise>
			<component:emptyComponent/>
		</c:otherwise>
	</c:choose>
</div>