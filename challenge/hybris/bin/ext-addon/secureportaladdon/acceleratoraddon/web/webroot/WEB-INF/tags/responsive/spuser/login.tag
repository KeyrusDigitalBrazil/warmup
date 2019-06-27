<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<div class="headline">
    <spring:theme code="login.title"/>
</div>

<form:form action="${action}" method="post" commandName="loginForm">
    <c:if test="${not empty message}">
		<span class="has-error"> <spring:theme code="${message}"/>
		</span>
    </c:if>

    <formElement:formInputBox idKey="j_username" labelKey="login.email" path="j_username" mandatory="true"/>
    <formElement:formPasswordBox idKey="j_password" labelKey="login.password" path="j_password" inputCSS="form-control"
                                 mandatory="true"/>

    <div class="forgotten-password">
        <ycommerce:testId code="login_forgotPassword_link">
        	<spring:url value="/login/pw/request" var="forgottenPwdUrl" htmlEscape="false"/>
            <a href="#" data-link="${fn:escapeXml(forgottenPwdUrl)}" class="js-password-forgotten"
               data-cbox-title="<spring:theme code="forgottenPwd.title"/>">
                <spring:theme code="login.link.forgottenPwd"/>
            </a>
        </ycommerce:testId>
    </div>

    <div class="row login-form-action">
        <c:set var="loginBtnClasses" value="col-sm-12 col-md-6 col-md-push-6"/>

        <c:if test="${enableRegistration}">
            <c:set var="loginBtnClasses" value="col-sm-6 col-sm-push-6"/>
        </c:if>

        <div class="${fn:escapeXml(loginBtnClasses)}">
            <ycommerce:testId code="login_Login_button">
                <button type="submit" class="btn btn-primary btn-block">
                    <spring:theme code="${actionNameKey}"/>
                </button>
            </ycommerce:testId>
        </div>

        <div class="col-sm-6 col-sm-pull-6">
            <c:if test="${enableRegistration}">
            	<spring:url value="/register" var="registerUrl" htmlEscape="false"/>
                <a href="${fn:escapeXml(registerUrl)}">
                    <button type="button" class="btn btn-default btn-block">
                        <spring:theme code="text.secureportal.link.createAccount"/>
                    </button>
                </a>
            </c:if>
        </div>
    </div>
</form:form>

