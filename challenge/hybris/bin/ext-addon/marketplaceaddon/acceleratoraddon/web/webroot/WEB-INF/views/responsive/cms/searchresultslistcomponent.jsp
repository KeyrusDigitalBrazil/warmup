<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="productmarketplace" tagdir="/WEB-INF/tags/addons/marketplaceaddon/responsive/product"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="product__list--wrapper">
	
    <div class="results">
        <spring:theme var="searchTextHtnl" code="search.page.searchText" arguments="${searchPageData.freeTextSearch}" htmlEscape="false"/>
        <h1>${ycommerce:sanitizeHTML(searchTextHtnl)}</h1>
    </div>

    <nav:searchSpellingSuggestion spellingSuggestion="${searchPageData.spellingSuggestion}" />

    <nav:pagination top="true"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>
    <ul class="product__listing product__list">
            <c:forEach items="${searchPageData.results}" var="product">
                 <productmarketplace:productListerItem product="${product}"/>
            </c:forEach>
    </ul>

    <div id="addToCartTitle" class="display-none">
        <div class="add-to-cart-header">
            <div class="headline">
                <span class="headline-text"><spring:theme code="basket.added.to.basket"/></span>
            </div>
        </div>
    </div>

    <nav:pagination top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"  numberPagesShown="${numberPagesShown}"/>
</div>
