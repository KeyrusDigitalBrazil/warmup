<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<button type="submit" id="cpqGoToCartBtn"
	class="cpq-btn-goToCart" aria-describedby="priceTotalLabel currentTotalValue">
	<spring:theme code="cart.checkout" />
</button>

