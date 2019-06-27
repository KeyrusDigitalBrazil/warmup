<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="confUtil" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="csticFilterList" required="true" type="java.util.List"%>
<%@ attribute name="groupFilterList" required="true" type="java.util.List"%>


<div class="facet js-facet">
	<div class="facet__name js-facet__name">
		<span class="glyphicon facet__arrow"></span>
		<spring:message code="sapproductconfig.overview.filter.applied.title" text="Applied Filters"/>
	</div>

	<div class="facet__values js-facet__values">
		<ul class="facet__list"> <!--  js-facet__list  -->
			<c:forEach var="csticFilter" items="${csticFilterList}" varStatus="status">
				<c:if test='${csticFilter.selected == true}'>
					<c:choose>
						<c:when test="${csticFilter.key eq confUtil:generalGroupName()}">
							<spring:message code="sapproductconfig.group.general.title" text="General (Default)" var="langname" />
						</c:when>
						<c:otherwise>
							<spring:message code="sapproductconfig.overview.filter.cstic.langname.${csticFilter.key}" text="${csticFilter.key}" var="langname" />
						</c:otherwise>
					</c:choose>
					<li>
						${langname}&nbsp;<a href="#" data-filter-id="${csticFilter.key}_filter" aria-label="${langname}"><span class="glyphicon glyphicon-remove"></span></a>
					</li>
				</c:if>
			</c:forEach>			
			<c:forEach var="groupFilter" items="${groupFilterList}" varStatus="status">
				<c:if test="${groupFilter.selected == true}">
					<c:choose>
						<c:when test="${groupFilter.key eq confUtil:generalGroupName()}">
							<spring:message code="sapproductconfig.group.general.title" text="General (Default)" var="langname" />
						</c:when>
						<c:otherwise>
							<c:set value="${groupFilter.description}" var="langname"/>
						</c:otherwise>
					</c:choose>
					<li>
						${langname}&nbsp;<a href="#" data-filter-id="${groupFilter.key}_filter" aria-label="${langname}"><span class="glyphicon glyphicon-remove"></span></a>
					</li>
				</c:if>
			</c:forEach>
		</ul>
	</div>
</div>
