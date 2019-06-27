<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="csticKey" required="true" type="java.lang.String"%>
<%@ attribute name="csticValueKey" required="false" type="java.lang.String"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>
<%@ attribute name="message" required="true" type="de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData"%>
<%@ attribute name="messageIndex" required="true" type="java.lang.Integer" %>

<c:set var="csticMessageId" value="${csticKey}" />
<c:if test="${not empty csticValueKey}">
	<c:set var="csticMessageId" value="${csticKey}.${csticValueKey}" />
</c:if>
		
<c:set var="extendedMessageStyleClass" value="${cssConf:extendedMessageStyleClass(message)}" />    			
<c:if test="${not empty extendedMessageStyleClass}">
	<c:if test="${not empty message.extendedMessage || not empty message.endDate}">
		<c:set var="messageKey" value="${csticKey};${csticValueKey};${message.message}${message.extendedMessage}" />					
		<div id="${csticMessageId}.${messageIndex}.linkMoreDetails" class="cpq-promo-message-link" data-target="${messageKey}" role="link"
			<c:if test="${message.showExtendedMessage}">style="display:none"</c:if>>
			<spring:message code="sapproductconfig.message.promo.fullDetails" text="full detail..." />
		</div>
		<div id="${csticMessageId}.${messageIndex}.extendedMessage" class="${extendedMessageStyleClass}"
			<c:if test="${!message.showExtendedMessage}">style="display:none"</c:if>>
			<c:if test="${not empty message.extendedMessage}">
				<div>
					<spring:message code="${message.extendedMessage}" text="${message.extendedMessage}" />
				</div>
			</c:if>
			<c:if test="${not empty message.endDate}">
				<div>
					<spring:message code="sapproductconfig.promo.message.expire.date" text="(Offer expires {0})" arguments="${message.endDate}" />
				</div>
			</c:if>
			<div id="${csticMessageId}.${messageIndex}.linkLessDetails" class="cpq-promo-message-link" data-target="${messageKey}" role="link" aria-describedby="${csticMessageId}.${messageIndex}.extendedMessage">
				<spring:message code="sapproductconfig.message.promo.lessDetails" text="less details" />
			</div>
		</div>
		<form:input type="hidden" id="${csticMessageId}.${messageIndex}.showExtendedMessage"
			path="${pathPrefix}messages[${messageIndex}].showExtendedMessage" />
		<form:input type="hidden" value="${message.message}" path="${pathPrefix}messages[${messageIndex}].message" />
		<form:input type="hidden" value="${message.extendedMessage}" path="${pathPrefix}messages[${messageIndex}].extendedMessage" />
	</c:if>
</c:if>				




