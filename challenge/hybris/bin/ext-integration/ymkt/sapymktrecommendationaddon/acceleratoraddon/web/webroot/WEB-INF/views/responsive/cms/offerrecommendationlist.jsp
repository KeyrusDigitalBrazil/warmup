<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<jsp:useBean id="random" class="java.util.Random" scope="application" />

<c:set var="ulid" value="offerRecoUL${offerRecoId}" />
<c:set var="divId" value="div${random.nextInt(1000)}" />

<c:choose>
	<c:when test="${not empty offers}">
		<c:set var="offer" value="${offers[0]}"/> <%-- only show the first and most relevant offer --%>
		<div id="${ulid}" align="center" 
			data-offerreco-item="offerRecoItem"
			data-offerreco-offer-id="${offer.offerId}" 
			data-offerreco-offer-content-id="${offer.contentId}"
			data-offerreco-offer-component-id="${componentId}">

			<c:url value="${offer.targetLink}" var="offerUrl" />
			<c:choose>
				<c:when test="${empty offerUrl || offerUrl eq '#'}">
					<img class="js-responsive-image" src="${offer.contentSource}" title="${offer.contentDescription}" alt="${offer.contentDescription}">
				</c:when>
				<c:otherwise>
					<a href="${offerUrl}">
						<img class="js-responsive-image" src="${offer.contentSource}" title="${offer.contentDescription}" alt="${offer.contentDescription}">
					</a>
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>
		<component:emptyComponent />
	</c:otherwise>
</c:choose>