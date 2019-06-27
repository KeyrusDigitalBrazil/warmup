<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:choose>
    <c:when test="${nocustomermapping}">
        <spring:theme code="ticket.error.customerMapping"/>
    </c:when>
<c:otherwise>
    <y-main></y-main>
</c:otherwise>
</c:choose>

