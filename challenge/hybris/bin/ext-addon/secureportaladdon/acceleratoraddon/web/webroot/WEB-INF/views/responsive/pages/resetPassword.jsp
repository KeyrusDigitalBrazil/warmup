<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sptemplate" tagdir="/WEB-INF/tags/addons/secureportaladdon/responsive/sptemplate" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<sptemplate:page pageTitle="${pageTitle}">
	<user:updatePwd/>
</sptemplate:page>