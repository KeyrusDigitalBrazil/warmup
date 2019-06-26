<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="/mobile/register/bind" var="action" htmlEscape="false" />
<spring:url value="/verification-code/create-code" var="getVerificationCodeUrl" htmlEscape="false" />
<spring:url value="/" var="skipBtnUrl" htmlEscape="false" />
<template:page pageTitle="${pageTitle}">
	<div class="row">
		<div class="col-md-6">
			<div class="yCmsContentSlot login-left-content-slot">
				<div class="yCmsComponent login-left-content-component">
					<div class="login-section">
						<div class="headline">
							<spring:theme code="profile.register.title" />
						</div>
						<spring:theme var="registerDescriptionHtml" code="profile.register.description" htmlEscape="false"/>
						<p>
							${ycommerce:sanitizeHTML(registerDescriptionHtml)}
						</p>
						<form:form action="${action}" method="post" commandName="verificationCodeForm">
			
							<input type="hidden" id="after-send-btn-text" value="<spring:theme code='profile.register.btn.sent.text'/>"/>
							<input type="hidden" name="codeType" value="binding"/>
							
							<formElement:formInputBox idKey="mobileNumber" labelKey="profile.register.mobile" path="mobileNumber" placeholder="profile.register.mobile" />
							<formElement:formInputBox idKey="verificationCode" labelKey="profile.register.verificationCode" path="verificationCode" placeholder="profile.register.verificationCode" />
							
							<div class="consent-mobile-binding">
					            <spring:theme var="bindMobileContentHtml" code="mobile.binding.content.text" htmlEscape="false"/>
					            <label class="control-label uncased">
					                ${ycommerce:sanitizeHTML(bindMobileContentHtml)}
					            </label>
					        </div>
							
							<div class="row">
		                        <div class="col-sm-6 col-sm-push-6 binding-btn">
		                            <div class="accountActions">
	                                    <button type="submit" class="btn btn-primary btn-block">
											<spring:theme code="profile.register.btn.binding.text"/>
										</button>
		                            </div>
		                        </div>
		                        <div class="col-sm-6 col-sm-pull-6 binding-btn">
		                            <div class="accountActions">
	                                    <button id="register-code-btn" type="button" data-url="${fn:escapeXml(getVerificationCodeUrl)}" class="btn btn-default btn-block">
											<spring:theme code="profile.register.btn.send.text"/>
										</button>
		                            </div>
		                        </div>
		                        <div class="col-sm-12 binding-btn">
			                        <div class="accountActions">
										<a class="btn btn-primary btn-block" id="skip-btn" href="${fn:escapeXml(skipBtnUrl)}">
											<spring:theme code="profile.register.btn.skip.text"/>
										</a>
									</div>
								</div>
		                    </div>
						</form:form>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>