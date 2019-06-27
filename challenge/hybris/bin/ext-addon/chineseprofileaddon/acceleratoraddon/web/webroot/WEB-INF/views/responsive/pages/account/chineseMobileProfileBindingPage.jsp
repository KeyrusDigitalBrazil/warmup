<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="/verification-code/create-code" var="getVerificationCodeUrl" htmlEscape="false"/>
<spring:url value="/mobile/bind" var="bindUrl" htmlEscape="false"/>

<template:page pageTitle="${pageTitle}">
	<div class="row">
		<div class="col-md-6">
			<div class="yCmsContentSlot login-left-content-slot">
				<div class="yCmsComponent login-left-content-component">
					<div class="login-section">
						<div class="headline">
							<spring:theme code="profile.binding.title" />
						</div>
						<form:form action="${bindUrl}" method="post" commandName="verificationCodeForm">
			
							<input type="hidden" id="after-send-btn-text" value="<spring:theme code='profile.register.btn.sent.text'/>"/>
							<input type="hidden" name="codeType" value="binding"/>
							
							<div class="form-group">
								<label class="control-label" for="profile.mobileNumber">
									<spring:theme code="profile.register.mobile"/>
								</label>
								<c:if test="${not empty verificationCodeForm.mobileNumber}">
									<c:set var="mobileNumber" value="${fn:escapeXml(verificationCodeForm.mobileNumber)}" scope="session" />
								</c:if>
								<input id="mobileNumber" readonly="readonly" name="mobileNumber" class="text form-control" type="text" value="${mobileNumber}">
							</div>
							
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
	                                    <button id="profile-code-btn" type="button" data-url="${fn:escapeXml(getVerificationCodeUrl)}" class="btn btn-default btn-block">
											<spring:theme code="profile.register.btn.send.text"/>
										</button>
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