<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="url" required="true" type="java.lang.String"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<a class="btn btn-primary btn-block returnButton" href="${fn:escapeXml(url)}"><spring:theme code="punchout.return"/></a>