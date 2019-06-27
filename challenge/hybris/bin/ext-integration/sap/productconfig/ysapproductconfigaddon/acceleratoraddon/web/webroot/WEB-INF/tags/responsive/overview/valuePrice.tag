<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue" %>

<div class="col-xs-3">
    <div class="cpq-overview-price-section hidden-xs hidden-sm">
        <div class="cpq-overview-cstic-obsolete-price">
            <c:if test="${not empty cstic.obsoletePriceDescription}">
                <c:out value="${cstic.obsoletePriceDescription}"/>
            </c:if>
        </div>
        <div class="cpq-overview-cstic-price <c:if test="${not empty cstic.obsoletePriceDescription}">cpq-price-savings</c:if>">
            <c:if test="${not empty cstic.priceDescription}">
                <c:out value="${cstic.priceDescription}"/>
            </c:if>
        </div>
    </div>
    <div class="cpq-overview-compact-price-section visible-xs visible-sm">
        <div class="cpq-overview-cstic-obsolete-price">
            <c:if test="${not empty cstic.obsoletePriceDescription}">
                <c:out value="${cstic.obsoletePriceDescription}"/>
            </c:if>
        </div>
        <div class="cpq-overview-cstic-price <c:if test="${not empty cstic.obsoletePriceDescription}">cpq-price-savings</c:if>">
            <c:if test="${not empty cstic.priceDescription}">
                <c:out value="${cstic.priceDescription}"/>
            </c:if>
        </div>
    </div>
</div>
