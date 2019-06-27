<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="searchUrl" value="/vendors?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>
<c:choose>
	<c:when test="${not empty searchPageData.results}">			
<div class="account-orderhistory-pagination">
	 <nav:pagination top="true" msgKey="text.vendors.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}"  numberPagesShown="${numberPagesShown}"/>
</div>

<div class="vendor__listing vendor__grid">

	<c:forEach items="${searchPageData.results}" var="vendor" varStatus="vs">
		
		<spring:url value="${vendor.url}" var="vendorUrl" htmlEscape="false"/>
		<div class="vendor-item">
			<c:choose>
			<c:when test="${not empty vendor.logo}">
			<c:forEach items="${vendor.logo}" var="media">
				<c:if test="${media.format eq logoFormat}">
					<a href="${fn:escapeXml(vendorUrl)}" class="vendors-img-link"><img alt="" src="${fn:escapeXml(media.url)}" class="vendors-img" /></a>
				</c:if>
			</c:forEach>
			</c:when>
			<c:otherwise>
				<spring:theme code="img.missingProductImage.responsive.product"
							text="/" var="imagePath" />
						<c:choose>
							<c:when test="${originalContextPath ne null}">
								<c:url value="${imagePath}" var="imageUrl"
									context="${originalContextPath}" />									
							</c:when>
							<c:otherwise>
								<c:url value="${imagePath}" var="imageUrl" />
							</c:otherwise>
						</c:choose>
						<a href="${fn:escapeXml(vendorUrl)}" class="vendors-img-link"><img alt="" src="${fn:escapeXml(imageUrl)}" class="vendors-img" /></a>						
			</c:otherwise>
			</c:choose>
			<div class="vendor-Url">
				<a href="${fn:escapeXml(vendorUrl)}">${fn:escapeXml(vendor.name)}</a>
			</div>			
		</div>
		
	</c:forEach>
</div>

<div class="account-orderhistory-pagination">
	<nav:pagination top="false" msgKey="text.vendors.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}"  numberPagesShown="${numberPagesShown}"/>
</div>
</c:when>
<c:otherwise>
	<div class="vendor_no_reviews">
		<spring:theme code="text.vendors.page.novendors" text="No vendors found" />
	</div>
</c:otherwise>
</c:choose>	