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
<%@ attribute name="messages" type="java.util.List"%>

<c:set var="csticMessageId" value="${csticKey}" />
<c:if test="${not empty csticValueKey}">
	<c:set var="csticMessageId" value="${csticKey}.${csticValueKey}" />
</c:if>

<c:if test="${fn:length(messages) gt 0}">
	<div id="${csticMessageId}.messages" class="cpq-messages">
		<c:forEach var="message" items="${messages}" varStatus="status">
			<c:set var="messageIconStyleClass" value="${cssConf:messageIconStyleClass(message)}" />
			<div class="cpq-message">
				<c:if test="${not empty messageIconStyleClass}"><div class="${messageIconStyleClass}"></div></c:if>
				<div class="${cssConf:messageTextAdditionalStyleClass(message)}">
					<spring:message code="${message.message}" text="${message.message}" />
				</div>
				<config:extendedMessage csticKey="${csticKey}" csticValueKey="${csticValueKey}" pathPrefix="${pathPrefix}" message="${message}" messageIndex="${status.index}" />
			</div>
		</c:forEach>
	</div>
</c:if>
