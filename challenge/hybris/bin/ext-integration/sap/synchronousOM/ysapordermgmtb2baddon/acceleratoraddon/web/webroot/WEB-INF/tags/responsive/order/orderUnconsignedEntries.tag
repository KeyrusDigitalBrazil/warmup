<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="addon-order" tagdir="/WEB-INF/tags/addons/ysapordermgmtb2baddon/responsive/order" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="well well-quinary well-xs">
    <div class="well-headline">
        <spring:theme code="sap.custom.order.status.${order.condensedStatus}"/>
    </div>
    <div class="well-content">
        <div class="row">
            <div class="col-md-5 order-ship-to">
                <div class="label-order"><spring:theme code="text.account.order.shipto" /></div>
                <div class="value-order"><order:addressItem address="${order.deliveryAddress}"/></div>
            </div>
        </div>
    </div>

    <div class="well-content">
        <div class="row">
            <div class="col-md-5 order-shipping-method">
                <div class="label-order"><spring:theme code="text.shippingMethod" /></div>
                <div class="value-order">${orderData.deliveryMode.name}<br>${orderData.deliveryMode.description}</div>
            </div>
        </div>
    </div>
</div>

<ul class="item__list">
    <li class="hidden-xs hidden-sm">
        <ul class="item__list--header">
            <li class="item__toggle"></li>
            <li class="item__image"></li>
            <li class="item__info"><spring:theme code="basket.page.item"/></li>
            <li class="item__price"><spring:theme code="basket.page.price"/></li>
            <li class="item__quantity"><spring:theme code="basket.page.qty"/></li>
            <li class="item__total--column"><spring:theme code="basket.page.total"/></li>
        </ul>
    </li>
	<c:forEach items="${order.unconsignedEntries}" var="entry" varStatus="loop">
        <addon-order:orderEntryDetails orderEntry="${entry}" order="${order}" itemIndex="${loop.index}"/>
	</c:forEach>
</ul>
