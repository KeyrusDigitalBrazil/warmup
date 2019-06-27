<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<div role="tabpanel" class="tab-pane" id="activitySection">
	<div class="pageable-fragment asm-customer360-tab">

		<div class="asm-customer360-activity-tab asm-customer360-promotions">
			<div class="clearfix">
				<h3>
					<spring:theme code="text.asm.customercoupon.title" />
				</h3>
			</div>
			
			<ul class="nav nav-tabs nav-tabs--responsive menu_customer_coupons" role="tablist">
			    <li role="presentation" class="active" value="available">
			        <a href="#available" class="js-customer-360-tab">
			        	<spring:theme code="text.tab.customercoupon.available" />
			        </a>
			    </li>
			    <li role="presentation" value="sent">
			        <a href="#sent" class="js-customer-360-tab">
			        	<spring:theme code="text.tab.customercoupon.sent" />
			        </a>
			    </li>
			    <li class="nav-tabs-mobile-caret js-dropdown">
		            <a class="pull-right" href="#"><span class="caret"></span></a>
		        </li>
			</ul>
			
			<div class="coupon-search-box">
				<div class="input-group">
					<input type="text" id="search-input" class="form-control"/>
					<span class="input-group-btn"> 
						<button class="btn btn-link" id="coupons-search-btn">
							<span class="glyphicon glyphicon-search"></span>
						</button>
					</span>
				</div>
			</div>
			
			<div id="long-load-example">
            	<div class="loader">Loading..</div>
         	</div>
         	
         	<div id="customer-coupons"></div>
		</div>
	</div>
</div>

<script type="text/javascript">
	ACC.customer360.loadCoupons({type : 'available', text : ''});
	ACC.customer360.switchCouponTab();
	ACC.customer360.handleSearch();
</script>
