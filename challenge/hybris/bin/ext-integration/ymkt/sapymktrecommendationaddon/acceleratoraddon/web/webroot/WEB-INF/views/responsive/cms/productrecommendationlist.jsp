<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ulid" value="prodRecoUL${recoId}"/>

<jsp:useBean id="random" class="java.util.Random" scope="application"/>
<c:set var="divId" value="div${random.nextInt(1000)}"/>

<c:choose>
	<c:when test="${not empty productReferences}">
	    <div id="${divId}" class="carousel__component--headline">${fn:escapeXml(title)}</div>
	    <div id="${ulid}" class="carousel__component--carousel js-owl-carousel js-owl-default">
	        <c:forEach items="${productReferences}" var="product">
	        	<c:url value="${product.target.url}" var="productUrl"/>
	            <div class="carousel__item" 
	            data-prodreco-item="prodRecoItem" 
	            data-prodreco-item-code='${product.target.code}'
	            data-prodreco-item-component-id='${componentId}'>
	                <a href="${productUrl}">
	                    <div class="carousel__item--thumb">
	                        <product:productPrimaryImage product="${product.target}" format="product"/>
	                    </div>
	                    <div class="carousel__item--name">${fn:escapeXml(product.target.name)}</div>
	                    <div class="carousel__item--price"><format:fromPrice priceData="${product.target.price}"/></div>
	                </a>
	            </div>
	        </c:forEach>
	    </div>
	</c:when>
	<c:otherwise>
		<component:emptyComponent/>
	</c:otherwise>
</c:choose>
