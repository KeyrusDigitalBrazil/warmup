<!--
 [y] hybris Platform

 Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.

 This software is the confidential and proprietary information of SAP
 ("Confidential Information"). You shall not disclose such Confidential
 Information and shall use it only in accordance with the terms of the
 license agreement you entered into with SAP.
-->
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


    <div class="headline"><spring:theme code="text.account.entitlements" text="Access & Entitlements"/></div>

    <c:if test="${not empty grants}">
        <table class="entitlementsListTable">
            <thead>
                <tr>
                    <th><spring:theme code="text.account.entitlements.entitlementName" text="Entitlement Name"/></th>
                    <th><spring:theme code="text.account.entitlements.date.start" text="Start Date"/></th>
                    <th><spring:theme code="text.account.entitlements.date.end" text="End Date"/></th>
                    <th><spring:theme code="text.account.entitlements.status" text="Status"/></th>
                </tr>
            </thead>

            <tbody>
                <c:forEach items="${grants}" var="grant">
                    <tr class="entitlementsItem">
                        <td>
                            ${grant.name}
                        </td>
                        <td>
                            <fmt:formatDate value="${grant.startTime}" dateStyle="long" timeStyle="short" type="date"/>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${null == grant.endTime}">
                                    <spring:theme code="text.account.entitlements.date.end.unlimited" text="Unlimited"/>
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${grant.endTime}" dateStyle="long" timeStyle="short" type="date"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            ${grant.status}
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${empty grants}">
        <p class="emptyMessage"><spring:theme code="text.account.entitlements.noEntitlements" text="You have no any entitlements"/></p>
    </c:if>
