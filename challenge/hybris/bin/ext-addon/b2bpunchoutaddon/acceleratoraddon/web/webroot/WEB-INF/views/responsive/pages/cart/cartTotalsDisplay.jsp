<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>

<%-- Verified that there's a pre-existing bug regarding the setting of showTax; created issue  --%>

<div class="row">
    <div class="col-xs-12 col-md-7 col-lg-6 pull-right">
        <div class="cart-totals">
            <cart:cartTotals cartData="${cartData}" showTax="false"/>
            <cart:ajaxCartTotals/>
        </div>
    </div>
</div>
