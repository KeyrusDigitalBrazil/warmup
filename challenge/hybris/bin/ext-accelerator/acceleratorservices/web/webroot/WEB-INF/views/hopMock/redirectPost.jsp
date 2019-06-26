<%@ page session="false" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/_ui/hop-mock/css/common.css" var="stylesheetPath"/>
<c:url value="/_ui/hop-mock/images/favicon.ico" var="favIconPath"/>
<c:url value="/_ui/hop-mock/images/logo.png" var="imgLogoPath"/>
<c:url value="/_ui/hop-mock/images/spinner.gif" var="imgSpinnerPath"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Mocked Hosted Order Page</title>
	<link rel="shortcut icon" href="${fn:escapeXml(favIconPath)}" type="image/x-icon"/>
	<link rel="stylesheet" type="text/css" media="screen" href="${fn:escapeXml(stylesheetPath)}"/>
</head>
<body onload="${showDebugPage ? '' : 'document.hostedOrderPagePostForm.submit()'}">
	<div id="mockwrapper">
		<div id="mockpage">
			<div id="mockHeader">
				<div class="logo">
					<img alt="logo" src="${fn:escapeXml(imgLogoPath)}"/>
				</div>
			</div>
			<div style="clear: both;"></div>
			<div id="item_container_holder">
				<div class="item_container">
					<div id="debugWelcome">
						<h3>
							<img src="${fn:escapeXml(imgSpinnerPath)}"/>&nbsp;
							<spring:message code="text.header.wait"/>
							
						</h3>
					</div>
				</div>
				<c:if test="${showDebugPage}">
					<div class="item_container">
						<div id="infoBox">
							<h3>
								<spring:message code="text.header.debug"/>
							</h3>
						</div>
					</div>
				</c:if>
				<div class="item_container">
					<form:form id="hostedOrderPagePostForm" name="hostedOrderPagePostForm" action="${fn:escapeXml(postUrl)}" method="post">
						<div id="postFormItems">
							<dl>
								<c:forEach items="${postParams}" var="entry" varStatus="status">
									<c:choose>
										<c:when test="${showDebugPage}">
											<dt><label for="${fn:escapeXml(entry.key)}" class="required">${fn:escapeXml(entry.key)}</label></dt>
											<dd><input type="text" id="${fn:escapeXml(entry.key)}" name="${fn:escapeXml(entry.key)}" value="${fn:escapeXml(entry.value)}" tabindex="${fn:escapeXml(status.count + 1)}"/></dd>
										</c:when>
										<c:otherwise>
											<input type="hidden" id="${fn:escapeXml(entry.key)}" name="${fn:escapeXml(entry.key)}" value="${fn:escapeXml(entry.value)}" />
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</dl>
						</div>
						<c:if test="${showDebugPage}">
							<div class="rightcol">
								<spring:message code="button.submit" var="submitButtonLabel"/>
								<input id="button.submit" class="submitButtonText" type="submit" title="${fn:escapeXml(submitButtonLabel)}" value="${fn:escapeXml(submitButtonLabel)}"/>
							</div>
						</c:if>
					</form:form>
				</div>
			</div>
			<div style="clear: both;"></div>
			<div id="footer">
			</div>
		</div>
	 </div>
</body>
</html>
