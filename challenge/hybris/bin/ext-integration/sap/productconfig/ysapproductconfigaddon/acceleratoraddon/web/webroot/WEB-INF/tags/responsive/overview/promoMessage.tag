<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="messages" type="java.util.List" %>

<c:if test="${fn:length(messages) gt 0}">
    <div class="cpq-overview-messages">
        <c:forEach var="message" items="${messages}" varStatus="status">
            <div class="cpq-overview-message">
                <div class="cpq-promo-applied">
                    <spring:message code="${message.message}" text="${message.message}"/>
                </div>

                <c:if test="${not empty message.endDate}">
                    <div>
                        <spring:message code="sapproductconfig.promo.message.expire.date" text="(Offer expires {0})"
                                        arguments="${message.endDate}"/>
                    </div>
                </c:if>
            </div>
        </c:forEach>
    </div>
</c:if>
