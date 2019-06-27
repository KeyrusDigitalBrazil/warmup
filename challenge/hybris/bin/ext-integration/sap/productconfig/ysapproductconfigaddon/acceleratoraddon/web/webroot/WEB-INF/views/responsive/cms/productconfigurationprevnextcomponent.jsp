<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not config.singleLevel}">
	<div class="cpq-button-bar">
		<c:set var="activeGroup" value="${config.groupToDisplay.group}" />
		<div id="cpqPrvBtn" class="cpq-prev-navigation col-xs-6 col-md-6 col-lg-6">
			<button type="submit" class="cpq-previous-button"
				<c:if test="${activeGroup.firstOrLastGroup eq 'FIRST' or activeGroup.firstOrLastGroup eq 'ONLYONE'}">disabled</c:if>>
				<spring:message code="sapproductconfig.previous" text="Previous" />
			</button>
		</div>
		<div id="cpqNextBtn" class="cpq-next-navigation col-xs-6 col-md-6 col-lg-6">
			<button type="submit" class="cpq-next-button"
				<c:if test="${activeGroup.firstOrLastGroup eq 'LAST' or activeGroup.firstOrLastGroup eq 'ONLYONE'}">disabled</c:if>>
				<spring:message code="sapproductconfig.next" text="Next" />
			</button>
		</div>
	</div>
</c:if>
