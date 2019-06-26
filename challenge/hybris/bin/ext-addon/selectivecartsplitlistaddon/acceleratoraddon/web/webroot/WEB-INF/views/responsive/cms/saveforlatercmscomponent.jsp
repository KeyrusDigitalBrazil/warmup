<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="selectivecart" tagdir="/WEB-INF/tags/addons/selectivecartsplitlistaddon/responsive/selectivecartsplitlist" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="/cart/entries/check" var="checkUrl" htmlEscape="false"/>
<spring:url value="/cart/entries/uncheck" var="uncheckUrl" htmlEscape="false"/>

<c:if test="${not empty wishlist2Data.entries}">
<div class="cart-save-for-later">
     <spring:theme code="text.saveforlater" arguments="${wishlist2Data.entries.size()}"/>    
</div>
<div>
     <selectivecart:wishlist2Items wishlist2Data="${wishlist2Data}"/>
</div>
</c:if>


