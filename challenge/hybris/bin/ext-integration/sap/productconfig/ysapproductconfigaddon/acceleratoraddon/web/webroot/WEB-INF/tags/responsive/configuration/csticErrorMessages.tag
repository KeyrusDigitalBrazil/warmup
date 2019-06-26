<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="conf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="groupType" required="true" type="de.hybris.platform.sap.productconfig.facades.GroupType"%>
<%@ attribute name="bindResult" required="true" type="org.springframework.validation.BindingResult"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="key" required="true" type="java.lang.String"%>

<c:if test="${groupType ne 'CONFLICT'}">
	<c:set var="warnings" value="${conf:csticWarnings(bindResult, path)}" />

	<c:if test="${fn:length(warnings) gt 0}">
		<c:set var="conflictFound" value="false" />
		<c:forEach var="message" items="${warnings}" varStatus="status">
			<c:if test="${message.type != 'CONFLICT'}">
				<span id="${key}.warnings" class="cpq-warning"> <span class="cpq-warning-msg"> <spring:message
							code="${message.code}" arguments="${message.args}" text="${message.message}" />
				</span>
				</span>
			</c:if>
			<c:if test="${message.type == 'CONFLICT' && !conflictFound}">
				<c:set var="conflictFound" value="true" />
				<span id="${key}.conflicts" class="cpq-conflict"> <span class="cpq-error-sign">&#xe101;</span> <a
					class="cpq-conflict-link" href="#"> <spring:message code="sapproductconfig.cstic.conflict.message"
							text="Conflict Detected - Resolve Issues Now" />
				</a>
				</span>
			</c:if>
		</c:forEach>
	</c:if>
</c:if>

<c:set var="validationErrors" value="${conf:csticValidationError(bindResult, path)}" />
<c:if test="${fn:length(validationErrors) gt 0}">
	<span id="${key}.errors" class="cpq-error"> <c:forEach var="message" items="${validationErrors}" varStatus="status">
			<span class="cpq-error-msg"> <spring:message code="${message.code}" arguments="${message.args}" text="${message.message}" />
			</span>
		</c:forEach>
	</span>
</c:if>
