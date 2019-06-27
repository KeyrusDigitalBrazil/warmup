<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="wishlistOrders" required="true" type="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="selectivecart" tagdir="/WEB-INF/tags/addons/selectivecartaddon/responsive/selectivecart" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="errorStatus" value="<%= de.hybris.platform.catalog.enums.ProductInfoStatus.valueOf(\"ERROR\") %>" />

<c:if test="${empty cartData.rootGroups && not empty wishlistOrders}">

	<ul class="item__list item__list__cart">
	    <li class="hidden-xs hidden-sm">
	        <ul class="item__list--header">
	       		<li class="item__checkbox"></li>
	            <li class="item__toggle"></li>
	            <li class="item__image"></li>
	            <li class="item__info"><spring:theme code="basket.page.item"/></li>
	            <li class="item__price"><spring:theme code="basket.page.price"/></li>
	            <li class="item__quantity"><spring:theme code="basket.page.qty"/></li>
	            <li class="item__delivery"><spring:theme code="basket.page.delivery"/></li>
	            <li class="item__total--column"><spring:theme code="basket.page.total"/></li>
	            <li class="item__remove"></li>
	        </ul>
	    </li>
	</ul>
</c:if>

<c:if test="${not empty wishlistOrders}">	
	<ul class="item__list item__list__cart">
	    <selectivecart:wishlist2Entry entries="${wishlistOrders}"/>
	</ul>
</c:if>

