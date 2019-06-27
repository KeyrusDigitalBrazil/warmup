<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="csticFilterList" required="true" type="java.util.List"%>


<c:if test="${not empty csticFilterList}">
	<div class="facet js-facet">
		<div class="facet__name js-facet__name">
			<span class="glyphicon facet__arrow"></span>
			<spring:message code="sapproductconfig.overview.filter.cstic.title" text="Cstic Filters"/>
		</div>
	
		<div class="facet__values js-facet__values js-facet__form">
			<ul class="facet__list js-facet__list js-facet__top-values">
				<c:forEach var="csticFilter" items="${csticFilterList}" varStatus="status">
					<spring:message code="sapproductconfig.overview.filter.cstic.langname.${csticFilter.key}" text="${csticFilter.key}" var="langname" />
					<li>
						<label class="cpq-overview-filter-item">
							<form:checkbox id="${csticFilter.key}_filter" class="facet__checkbox sr-only"
								 path="csticFilterList[${status.index}].selected" value="${csticFilter.selected}" />
							<div class="facet__label">
								<div class="facet__list__mark col-xs-1 <c:if test='${csticFilter.selected == true}'>filter-selected</c:if>""></div>
								<div class="facet__list__text col-xs-10">${langname}</div>
							</div>
						</label>
					</li>
					<form:input type="hidden" value="${csticFilter.key}" path="csticFilterList[${status.index}].key" />
				</c:forEach>
			</ul>
		</div>
	</div>
</c:if>