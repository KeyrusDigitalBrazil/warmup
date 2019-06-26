<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="searchUrl" value="/my-company/organization-management/accountsummary-unit/details"/>
<spring:url var="accountSummaryUnitDetailsUrl" value="${searchUrl}" htmlEscape="false"/>
<c:set var="searchUrlWithParams"
       value="${searchUrl}?unit=${ycommerce:encodeUrl(accountSummaryInfoData.b2bUnitData.uid)}&sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}
&documentTypeCode=${ycommerce:encodeUrl(criteriaData.documentTypeCode)}&startRange=${ycommerce:encodeUrl(criteriaData.startRange)}&endRange=${ycommerce:encodeUrl(criteriaData.endRange)}
&documentStatus=${ycommerce:encodeUrl(criteriaData.documentStatus)}&filterByKey=${ycommerce:encodeUrl(filterByKey)}&filterByValue=${ycommerce:encodeUrl(criteriaData.filterByValue)}"/>

<jsp:useBean id="params" class="java.util.HashMap"/>
<c:set target="${params}" property="unit" value="${accountSummaryInfoData.b2bUnitData.uid}" />
<c:set target="${params}" property="sort" value="${searchPageData.pagination.sort}" />

<jsp:useBean id="additionalParams" class="java.util.HashMap"/>
<c:set target="${additionalParams}" property="unit" value="${accountSummaryInfoData.b2bUnitData.uid}" />
<c:set target="${additionalParams}" property="documentTypeCode" value="${criteriaData.documentTypeCode}" />
<c:set target="${additionalParams}" property="startRange" value="${criteriaData.startRange}" />
<c:set target="${additionalParams}" property="endRange" value="${criteriaData.endRange}" />
<c:set target="${additionalParams}" property="documentStatus" value="${criteriaData.documentStatus}" />
<c:set target="${additionalParams}" property="filterByKey" value="${filterByKey}" />
<c:set target="${additionalParams}" property="filterByValue" value="${criteriaData.filterByValue}" />

<div class="section-headline">
	<spring:theme code="text.company.accountsummary.documents.label"/>
</div>

<div class="row">
	<form:form id="filterByCriteriaForm" action="${accountSummaryUnitDetailsUrl}" method="get" class="clearfix account-summary-filter">
		<div class="documentStatus col-md-2 form-group">
			<label>
				<spring:theme code="text.company.accountsummary.criteria.document.status.label"/>
			</label>
			<select id="documentStatus" name="documentStatus" class="form-control">			
				<option id="all" value="ALL" ${ documentStatusItem eq criteriaData.documentStatus ? 'selected="selected"' : ''}>
					<spring:theme code="text.company.accountsummary.document.status.all.label"/>
				</option>
				<c:forEach items="${documentStatusList}" var="documentStatusItem">
					<option id="${fn:escapeXml(documentStatusItem)}"  value="${fn:escapeXml(documentStatusItem)}" ${ documentStatusItem eq criteriaData.documentStatus ? 'selected="selected"' : '' }>
						<spring:theme code="text.company.accountsummary.document.status.${documentStatusItem}.label"/>
					</option>
				</c:forEach>
			</select>
		</div>
		
		<div id="filterByKey" class="filterByKey col-md-3 form-group" data-filter-by-key="${fn:escapeXml(filterByKey)}">
			<label>
				<spring:theme code="text.company.accountsummary.criteria.filterby.label"/>
			</label>
			<select id="filterByKey" name="filterByKey" class="form-control">
				<c:forEach items="${filterByList}" var="filterByItem">
					<option id="${fn:escapeXml(filterByItem)}" value="${fn:escapeXml(filterByItem)}" ${filterByItem eq filterByKey ? 'selected="selected"' : ''}>
						<spring:theme code="text.company.accountsummary.${filterByItem}.label"/>
					</option>
				</c:forEach>
			</select>
		</div>
		
		<div class="filterByValue col-md-5">
            <c:set var="startRangeCriteriaHtml" value="${fn:escapeXml(criteriaData.startRange)}"/>
            <c:set var="endRangeCriteriaHtml" value="${fn:escapeXml(criteriaData.endRange)}"/>
            <div id="rangeCriteria" class="rangeCriteria criterias row" style="display: none;"
				data-date-format-for-date-picker="${fn:escapeXml(dateFormat)}"
				data-start-Range="${startRangeCriteriaHtml}"
				data-end-Range="${endRangeCriteriaHtml}">

                <div class="col-sm-6 col-md-4 form-group">
                    <label>
                        <spring:theme code="text.company.accountsummary.from.label"/>
                    </label>
                    <div id="startRangeCriteria" class="date-input">
                        <input id="startRange" class="form-control filterCriteria" name="startRange" type="text" value="${startRangeCriteriaHtml}"/>
                        <i class=""></i>
                    </div>
                </div>
                <div class="col-sm-6 col-md-4 form-group">
                    <label>
                        <spring:theme code="text.company.accountsummary.to.label"/>
                    </label>
                    <div id="endRangeCriteria" class="date-input">
                        <input id="endRange" class="form-control filterCriteria" name="endRange" type="text" value="${endRangeCriteriaHtml}"/>
                        <i class=""></i>
                    </div>
                </div>
			</div>
			<div id="documentTypeCriteria" class="documentTypeCriteria criterias col-md-7 form-group" style="display: none;"
				data-document-type-code="${fn:escapeXml(criteriaData.documentTypeCode)}">
                <div class="row">
                    <label>
                        <spring:theme code="text.company.accountsummary.documentType.label"/>
                    </label>
                    <select id="documentTypeCode" name="documentTypeCode" class="form-control filterCriteria">
                        <option disabled="disabled" value="" ${ empty criteriaData.documentTypeCode ? 'selected="selected"' : ''}>
                            <spring:theme code="text.company.accountsummary.document.type.select.label"/>
                        </option>
                        <c:forEach items="${documentTypeList}" var="documentType">
                            <c:set var="documentTypeCodeHtml" value="${fn:escapeXml(documentType.code)}"/>
                            <option id="${documentTypeCodeHtml}" value="${documentTypeCodeHtml}" ${documentType.code eq criteriaData.documentTypeCode ? 'selected="selected"' : ''}>${fn:escapeXml(documentType.name)}</option>
                        </c:forEach>
                    </select>
			    </div>
			</div>
            <c:set var="filterByValueCriteriaHtml" value="${fn:escapeXml(criteriaData.filterByValue)}"/>
            <div id="singleValueCriteria" class="singleValueCriteria criterias col-md-7 form-group" style="display: none;"
				data-filter-by-value="${filterByValueCriteriaHtml}">
                <div class="row">
                    <label>
                        <spring:theme code="text.company.accountsummary.documentNumber.label"/>
                    </label>
                    <div id="singleValue">
                        <input id="filterByValue" class="form-control filterCriteria" name="filterByValue" type="text" value="${filterByValueCriteriaHtml}"/>
                    </div>
                </div>
			</div>
		</div>
        <div class="col-md-2">
            <button id="search" type="submit" value="Search" class="btn btn-primary btn-block">
                <spring:theme code="text.company.accountsummary.button.search"/>
            </button>
        </div>
        <c:if test="${not empty params}">
			<c:forEach items="${params}" var="entry">
				<input type="hidden" name="${fn:escapeXml(entry.key)}" value="${fn:escapeXml(entry.value)}"/>
			</c:forEach>
		</c:if>
	</form:form>
</div>

<c:choose>
	<c:when test="${empty searchPageData.results}">
		<div class="account-section-content	account-section-content-small content-empty">
			<spring:theme code="text.company.accountsummary.nodocuments.info" />
		</div>
	</c:when>
	<c:otherwise>
		<div class="account-section-content	">
			<div class="account-orderhistory">
				<div class="account-orderhistory-pagination">
					<nav:pagination top="true" msgKey="text.company.accountsummary.unit.details.page" showCurrentPageInfo="true" hideRefineButton="true"
					supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrlWithParams}"
					numberPagesShown="${numberPagesShown}" additionalParams="${additionalParams}"/>
				</div>
                <div class="account-summary-table">
                    <table class="responsive-table">
                        <tr class="responsive-table-head hidden-xs">
                            <th><spring:theme code="text.company.accountsummary.documentNumber.label" /></th>
                            <th><spring:theme code="text.company.accountsummary.documentType.label"/></th>
                            <th><spring:theme code="text.company.accountsummary.date.label"/></th>
                            <th><spring:theme code="text.company.accountsummary.dueDate.label"/></th>
                            <th><spring:theme code="text.company.accountsummary.amount.label"/></th>
                            <th><spring:theme code="text.company.accountsummary.openAmount.label"/></th>
                            <th><spring:theme code="text.company.accountsummary.status.label"/></th>
                            <th><spring:theme code="text.company.accountsummary.document.attachment.label"/></th>
                        </tr>
                        <c:forEach items="${searchPageData.results}" var="result">
                            <tr class="responsive-table-item">
                                    <td class="hidden-sm hidden-md hidden-lg">
                                        <spring:theme code="text.company.accountsummary.documentNumber.label" />
                                    </td>
                                    <td class="responsive-table-cell">
                                        ${fn:escapeXml(result.documentNumber)}
                                    </td>
                                    <td class="hidden-sm hidden-md hidden-lg">
                                        <spring:theme code="text.company.accountsummary.documentType.label"/>
                                    </td>
                                    <td class="responsive-table-cell">
                                        ${fn:escapeXml(result.documentType.name)}
                                    </td>
                                    <td class="hidden-sm hidden-md hidden-lg">
                                        <spring:theme code="text.company.accountsummary.date.label"/>
                                    </td>
                                    <td class="responsive-table-cell">
                                        <fmt:formatDate value="${result.date}" type="date"/>
                                    </td>
                                    <td class="hidden-sm hidden-md hidden-lg">
                                        <spring:theme code="text.company.accountsummary.dueDate.label"/>
                                    </td>
                                    <td class="responsive-table-cell">
                                    	<fmt:formatDate value="${result.dueDate}" type="date"/>
                                    </td>
                                    <td class="hidden-sm hidden-md hidden-lg">
                                        <spring:theme code="text.company.accountsummary.amount.label"/>
                                    </td>
                                    <td class="responsive-table-cell">
                                        ${fn:escapeXml(result.formattedAmount)}
                                    </td>
                                    <td class="hidden-sm hidden-md hidden-lg">
                                        <spring:theme code="text.company.accountsummary.openAmount.label"/>
                                    </td>
                                    <td class="responsive-table-cell">
                                        ${fn:escapeXml(result.formattedOpenAmount)}
                                    </td>
                                    <td class="hidden-sm hidden-md hidden-lg">
                                        <spring:theme code="text.company.accountsummary.status.label"/>
                                    </td>
                                    <td class="responsive-table-cell">
                                        <spring:theme code="text.company.accountsummary.${result.status}.label"/>
                                    </td>
                                    <td class="hidden-sm hidden-md hidden-lg">
                                        <spring:theme code="text.company.accountsummary.document.attachment.label"/>
                                    </td>
                                    <td class="responsive-table-cell">
                                        <c:if test="${not empty result.documentMedia.downloadURL}">
                                            <a class="download-lnk" href="${fn:escapeXml(result.documentMedia.downloadURL)}" target="_blank">
                                                <spring:theme code="text.company.accountsummary.document.attachment.view.label"/>
                                            </a>
                                        </c:if>
                                    </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
			</div>
			<div class="account-orderhistory-pagination">
				<nav:pagination top="false" msgKey="text.company.accountsummary.unit.details.page" showCurrentPageInfo="true" hideRefineButton="true"
					supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrlWithParams}"
					numberPagesShown="${numberPagesShown}" additionalParams="${additionalParams}"/>
			</div>
		</div>
	</c:otherwise>
</c:choose>
