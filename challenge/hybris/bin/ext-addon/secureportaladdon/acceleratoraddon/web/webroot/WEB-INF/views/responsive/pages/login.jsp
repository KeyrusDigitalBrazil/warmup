<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sptemplate" tagdir="/WEB-INF/tags/addons/secureportaladdon/responsive/sptemplate" %>
<%@ taglib prefix="spuser" tagdir="/WEB-INF/tags/addons/secureportaladdon/responsive/spuser" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<sptemplate:page pageTitle="${pageTitle}">
	<jsp:body>
        <div class="row">
            <div class="col-sm-offset-2 col-sm-8 col-md-offset-3 col-md-6">
                <div class="login-section">
                    <c:url value="/j_spring_security_check" var="loginActionUrl"/>
                    <spuser:login actionNameKey="login.login" action="${loginActionUrl}"/>
                </div>
            </div>
        </div>
	</jsp:body>
</sptemplate:page>
