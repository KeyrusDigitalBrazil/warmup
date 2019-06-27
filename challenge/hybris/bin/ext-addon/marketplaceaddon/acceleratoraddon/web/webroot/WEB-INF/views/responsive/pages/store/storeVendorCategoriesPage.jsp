<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="nav_column product__facet js-product-facet hidden-sm hidden-xs" id="product-facet">
  <ycommerce:testId code="categoryNav_category_links">
	<div class="facet js-facet active">
    	<div class="facet__name js-facet-name hidden-xs hidden-sm"><spring:theme code="search.nav.categoryNav"/>
        	<span class="facet__value__count">
            	<spring:theme code="search.nav.facetValueCount" arguments="${fn:length(vendorData.categories)}"/>
            </span>
        </div>

			<div class="facet__values js-facet-values js-facet-form">
				<c:if test="${not empty vendorData.topCategories}">
					<ul class="facet__list js-facet-list js-facet-top-values">
						<c:forEach items="${vendorData.topCategories}" var="category">
							<li>
								<c:url value="${category.url}" var="categoryUrl"/>
                                <span class="facet__text">
									<a href="${fn:escapeXml(categoryUrl)}">${fn:escapeXml(category.name)}</a>&nbsp;
                                        <span class="facet__value__count"></span>
								</span>
							</li>
						</c:forEach>
					</ul>
				</c:if>
				<ul class="facet__list test js-facet-list <c:if test="${not empty vendorData.topCategories}">facet__list--hidden js-facet-list-hidden</c:if>" style="display: none;">
						<c:forEach items="${vendorData.categories}" var="category">
							<li>
								<c:url value="${category.url}" var="categoryUrl"/>
                                <span class="facet__text">
									<a href="${fn:escapeXml(categoryUrl)}">${fn:escapeXml(category.name)}</a>&nbsp;
                                        <span class="facet__value__count"></span>
								</span>
							</li>
						</c:forEach>
					</ul>
			<c:if test="${fn:length(vendorData.topCategories) lt fn:length(vendorData.categories)}">
				<span class="facet__values__more js-more-facet-values">
					<a href="#" class="js-more-facet-values-link" style="font-weight:bold"><spring:theme code="search.nav.vendor.facetShowMore_category" /></a>
				</span>
				<span class="facet__values__less js-less-facet-values">
					<a href="#" class="js-less-facet-values-link" style="font-weight:bold"><spring:theme code="search.nav.vendor.facetShowLess_category" /></a>
				</span>
			</c:if>
	 </div>
   </div>
 </ycommerce:testId>
</div>