<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="wishlist2Data" required="true" type="de.hybris.platform.selectivecartfacades.data.Wishlist2Data" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="selectivecart" tagdir="/WEB-INF/tags/addons/selectivecartsplitlistaddon/responsive/selectivecartsplitlist" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="errorStatus" value="<%= de.hybris.platform.catalog.enums.ProductInfoStatus.valueOf(\"ERROR\") %>" />

<c:if test="${not empty wishlist2Data.entries}">

	<ul class="item__list item__list__cart">
	    <li class="hidden-xs hidden-sm">
	        <ul class="item__list--header">
	            <li class="item__toggle"></li>
	            <li class="item__image"></li>
	            <li class="item__info"><spring:theme code="basket.page.item"/></li>
	            <li class="item__price"><spring:theme code="basket.page.price"/></li>
	            <li class="item__quantity"><spring:theme code="basket.page.qty"/></li>
	            <li class="item__action"></li>
	        </ul>
	    </li>
    <selectivecart:wishlist2Entry wishlist2Data="${wishlist2Data}" entries="${wishlist2Data.entries}"/>
    </ul>
</c:if>


