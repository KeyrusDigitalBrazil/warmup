<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ attribute name="groupFilterList" required="true" type="java.util.List"%>
<%@ taglib prefix="confUtil" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<c:if test="${not empty groupFilterList}">
	<div class="facet js-facet">
		<div class="facet__name js-facet__name">
			<span class="glyphicon facet__arrow"></span>
			<spring:message code="sapproductconfig.overview.filter.group.title" text="Group Filters"/>
		</div>
	
		<div class="facet__values js-facet__values js-facet__form">
			<ul class="facet__list js-facet__list js-facet__top-values">
				<c:forEach var="groupFilter" items="${groupFilterList}" varStatus="status">
					<c:choose>
						<c:when test="${groupFilter.key eq confUtil:generalGroupName()}">
							<spring:message code="sapproductconfig.group.general.title" text="General (Default)" var="langname" />
						</c:when>
						<c:otherwise>
							<c:set value="${groupFilter.description}" var="langname"/>
						</c:otherwise>
					</c:choose>
					<li>
						<label class="cpq-overview-filter-item">
							<form:checkbox id="${groupFilter.key}_filter" class="facet__checkbox sr-only"
					 			path="groupFilterList[${status.index}].selected" value="${groupFilter.selected}" />
							<div class="facet__label">
								<div class="facet__list__mark col-xs-1 <c:if test='${groupFilter.selected == true}'>filter-selected</c:if>"></div>
								<div class="facet__list__text col-xs-10">${langname}</div>
							</div>
						</label>
					</li>
					<form:input type="hidden" value="${groupFilter.description}" path="groupFilterList[${status.index}].description" />
					<form:input type="hidden" value="${groupFilter.key}" path="groupFilterList[${status.index}].key" />
				</c:forEach>
			</ul>
		</div>
	</div>
</c:if>
