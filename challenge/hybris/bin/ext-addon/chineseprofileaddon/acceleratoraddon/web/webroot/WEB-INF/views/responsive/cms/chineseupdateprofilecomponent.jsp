<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="/mobile/binding" var="bindUrl" htmlEscape="false"/>
<spring:url value="/mobile/unbind" var="unbindUrl" htmlEscape="false"/>

<div class="account-section-header">
    <div class="row">
        <div class="container-lg col-md-6">
            <spring:theme code="text.account.profile.updatePersonalDetails"/>
        </div>
    </div>
</div>
<div class="row">
    <div class="container-lg col-md-6">
        <div class="account-section-content">
            <div class="account-section-form">
		        <form:form action="update-profile" method="post" commandName="updateProfileForm">
		
		            <formElement:formSelectBox idKey="profile.title" labelKey="profile.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titleData}" selectCSSClass="form-control"/>
                    <formElement:formInputBox idKey="profile.firstName" labelKey="profile.firstName" path="firstName" inputCSS="text" mandatory="true"/>
                    <formElement:formInputBox idKey="profile.lastName" labelKey="profile.lastName" path="lastName" inputCSS="text" mandatory="true"/>
                    <input type="hidden" id="mobileInvalidText" value="<spring:theme code='register.mobileNumber.invalid'/>"/>
					
					<div class="row">
                        <div class="col-sm-6 col-sm-push-6">
                            <div class="accountActions">
                                <ycommerce:testId code="personalDetails_savePersonalDetails_button">
                                    <button type="submit" class="btn btn-primary btn-block">
                                        <spring:theme code="text.account.profile.saveUpdates"/>
                                    </button>
                                </ycommerce:testId>
                            </div>
                        </div>
                        <div class="col-sm-6 col-sm-pull-6">
                            <div class="accountActions">
                                <ycommerce:testId code="personalDetails_cancelPersonalDetails_button">
                                    <button type="button" class="btn btn-default btn-block backToHome">
                                        <spring:theme code="text.account.profile.cancel"/>
                                    </button>
                                </ycommerce:testId>
                            </div>
                        </div>
                    </div>
		        </form:form>
		        <br/>
		        <hr>
		        <form:form action="${bindUrl}" method="post" commandName="verificationCodeForm">
					<div class="form-group">
						<label class="control-label" for="mobileNumber"> 
							<spring:theme code="profile.mobileNumber" />
						</label> 
						<input id="mobileNumber" name="mobileNumber" class="text form-control" type="text" 
							value="${fn:escapeXml(user.mobileNumber)}" <c:if test="${not empty user.mobileNumber}">readonly="readonly"</c:if>>
					</div>
					<div class="accountActions">
						<c:choose>
							<c:when test="${empty user.mobileNumber}">
								<button type="button" id="bind-btn" data-url="${fn:escapeXml(bindUrl)}"
									class="btn btn-block btn-primary">
									<spring:theme code="mobile.binding.button.text" />
								</button>
							</c:when>
							<c:otherwise>
								<button type="button" id="unbind-btn" data-url="${fn:escapeXml(unbindUrl)}"
									class="btn btn-block btn-default">
									<spring:theme code="mobile.unbinding.button.text" />
								</button>
							</c:otherwise>
						</c:choose>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
