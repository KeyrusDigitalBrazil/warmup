<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="addon-order" tagdir="/WEB-INF/tags/addons/ysapordermgmtb2baddon/responsive/order" %>

<div class="account-orderdetail">
	<div class="account-orderdetail-item-section-header">
		<div>
			<addon-order:orderTotalsItem order="${orderData}"/>
			
			
		</div>
	</div>
</div>