<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/b2bacceleratoraddon/responsive/order" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="addon-order" tagdir="/WEB-INF/tags/addons/ysapordermgmtb2baddon/responsive/order" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="account-orderdetail account-consignment">
    <ycommerce:testId code="orderDetail_itemList_section">
        <c:if test="${not empty orderData.unconsignedEntries}">
            <addon-order:orderUnconsignedEntries order="${orderData}"/>
        </c:if>
        <c:forEach items="${orderData.consignments}" var="consignment">
            <c:if test="${consignment.status.code eq 'WAITING' or consignment.status.code eq 'PICKPACK' or consignment.status.code eq 'READY'}">
                <div class="fulfilment-states-${consignment.status.code}">
                    <addon-order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}" inProgress="true"/>
                </div>
            </c:if>
        </c:forEach>
        <c:forEach items="${orderData.consignments}" var="consignment">
            <c:if test="${consignment.status.code ne 'WAITING' and consignment.status.code ne 'PICKPACK' and consignment.status.code ne 'READY'}">
                <div class="fulfilment-states-${consignment.status.code}">
                    <addon-order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}"/>
                </div>
            </c:if>
        </c:forEach>
    </ycommerce:testId>
</div>

