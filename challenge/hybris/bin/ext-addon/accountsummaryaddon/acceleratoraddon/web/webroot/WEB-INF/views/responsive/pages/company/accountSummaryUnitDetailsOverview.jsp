<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>	

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="account-section-content">
    <div class="well well-lg well-tertiary">
        <div class="col-md-7 col-lg-8 account-summary-detail clearfix">
            <div class="col-sm-4 col-md-6 col-lg-4 item-wrapper">
                <div class="item-group">
                    <span class="item-label"><spring:theme code="text.company.accountsummary.businessunitid.label"/></span>
                    <span class="item-value">${fn:escapeXml(accountSummaryInfoData.b2bUnitData.uid)}</span>
                </div>
                <div class="item-group">
                    <span class="item-label"><spring:theme code="text.company.accountsummary.b2bunit.label"/></span>
                    <span class="item-value">${fn:escapeXml(accountSummaryInfoData.b2bUnitData.name)}</span>
                </div>
                <div class="item-group">
                	<span class="item-label"><spring:theme code="text.company.accountsummary.address.label"/></span>
                    <c:if test="${not empty accountSummaryInfoData.billingAddress}">
                        <span class="item-value">
                            ${fn:escapeXml(accountSummaryInfoData.billingAddress.title)},&nbsp;${fn:escapeXml(accountSummaryInfoData.billingAddress.firstName)}&nbsp;${fn:escapeXml(accountSummaryInfoData.billingAddress.lastName)}<br/>
                            ${fn:escapeXml(accountSummaryInfoData.billingAddress.formattedAddress)}<br/>
                            ${fn:escapeXml(accountSummaryInfoData.billingAddress.country.name)}
                        </span>
                    </c:if>
                </div>
            </div>
            <div class="col-sm-4 col-md-6 col-lg-4 item-wrapper">
                <div class="item-group">
					<span class="item-label"><spring:theme code="text.company.accountsummary.creditrep.label"/></span>
					<span class="item-value">
					<c:set var="accountManagerName" value="${accountSummaryInfoData.accountManagerName}"/>
					<c:set var="accountManagerEmail" value="${accountSummaryInfoData.accountManagerEmail}"/>
						<c:choose>
							<c:when test="${not empty accountManagerName}">
								<c:if test="${not empty accountManagerEmail}">
	                                <a href="mailto:${fn:escapeXml(accountManagerEmail)}" target="_top">
	                            </c:if>
	                            ${fn:escapeXml(accountManagerName)}
	                            <c:if test="${not empty accountManagerEmail}">
	                                </a>
	                            </c:if>
							</c:when>
							<c:otherwise>
								<spring:theme code="text.company.accountsummary.not.applicable"/>
							</c:otherwise>
						</c:choose>
					</span>
                </div>
                <div class="item-group">
                	<span class="item-label"><spring:theme code="text.company.accountsummary.creditline.label"/></span>
                    <span class="item-value">
	                    <c:choose>
							<c:when test="${not empty accountSummaryInfoData.formattedCreditLimit}">
								${fn:escapeXml(accountSummaryInfoData.formattedCreditLimit)}
	                    	</c:when>
							<c:otherwise>
								<spring:theme code="text.company.accountsummary.not.applicable"/>
							</c:otherwise>
						</c:choose>
					</span>
                </div>
            </div>

            <div class="col-sm-4 col-md-6 col-lg-4 item-wrapper">
                <div class="item-group">
                    <span class="item-label"><spring:theme code="text.company.accountsummary.currentbalance.label"/></span>
                    <span class="item-value">
                    	<c:choose>
	                    	<c:when test="${not empty accountSummaryInfoData.amountBalanceData.currentBalance}">
	                    		${fn:escapeXml(accountSummaryInfoData.amountBalanceData.currentBalance)}
	                   		</c:when>
	                   		<c:otherwise>
								<spring:theme code="text.company.accountsummary.not.applicable"/>
							</c:otherwise>
						</c:choose>
                    </span>
                </div>
                <div class="item-group">
                    <span class="item-label"><spring:theme code="text.company.accountsummary.openbalance.label"/></span>
                    <span class="item-value">
                    	<c:choose>
	                    	<c:when test="${not empty accountSummaryInfoData.amountBalanceData.openBalance}">
	                    		${fn:escapeXml(accountSummaryInfoData.amountBalanceData.openBalance)}
	                   		</c:when>
	                    	<c:otherwise>
								<spring:theme code="text.company.accountsummary.not.applicable"/>
							</c:otherwise>
						</c:choose>
                    </span>
                </div>
            </div>
        </div>

        <div class="col-md-5 col-lg-4 item-wrapper clearfix">
            <div class="framed">
                <c:forEach items="${accountSummaryInfoData.amountBalanceData.dueBalance}" var="range">
                    <c:choose>
                        <c:when test="${empty range.key.maxBoundery}">
                            <c:set var="maxBoundery" value="+"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="maxBoundery" value="-${fn:escapeXml(range.key.maxBoundery)}"/>
                        </c:otherwise>
                    </c:choose>
                    <span class="item-label">
                        ${fn:escapeXml(range.key.minBoundery)} ${fn:escapeXml(maxBoundery)}&nbsp;
                        <spring:theme code="text.company.accountsummary.days.label"/>
                    </span>
                    <span class="item-value">
                        ${fn:escapeXml(range.value)}
                    </span>
                </c:forEach>

                <div class="item-group total">
                    <span class="item-label">
                        <spring:theme code="text.company.accountsummary.pastduebalance.label"/>
                    </span>
                    <span class="item-value">
                        ${fn:escapeXml(accountSummaryInfoData.amountBalanceData.pastDueBalance)}
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>