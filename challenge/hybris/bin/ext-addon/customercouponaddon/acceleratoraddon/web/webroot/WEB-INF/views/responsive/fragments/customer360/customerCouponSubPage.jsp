<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />


<c:choose>
	<c:when test="${empty customerCoupons}">
		<div class="asm__customer360-noCoupons">
			<spring:theme code="text.asm.${type}.nocustomercoupons"/>
		</div>
	</c:when>
	<c:otherwise>
		<c:forEach items="${customerCoupons}" var="coupon">
			<div class="asm-customer360-promotions-item clearfix">
				<div class="asm-customer360-promotions-item-title">
					<spring:theme code="text.asm.customercoupon.idname" >
						<spring:argument>${coupon.name}</spring:argument>
						<spring:argument>${coupon.couponCode}</spring:argument>
					</spring:theme>
				</div>
				<div id="${fn:escapeXml(coupon.couponCode)}show">
					<spring:url value="/asm/customer-coupon/{/couponCode}" var="actionUrl" htmlEscape="false">
						<spring:param name="couponCode" value="${coupon.couponCode}" />
					</spring:url>
					<a href="javascript:;" id="${fn:escapeXml(coupon.couponCode)}" class="asm-customer360-promotions-addToCart js-coupons-action" 
							data-action="${fn:escapeXml(action)}" data-url="${fn:escapeXml(actionUrl)}">
						<spring:theme code="text.asm.customercoupon.${action}"/>
					</a>
				</div>
				<div class="asm-customer360-promotions-item-desc">
					<spring:theme code="${coupon.description}"/>
				</div>
			</div>
		</c:forEach>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
	ACC.customer360.handleCouponAction();
</script>
