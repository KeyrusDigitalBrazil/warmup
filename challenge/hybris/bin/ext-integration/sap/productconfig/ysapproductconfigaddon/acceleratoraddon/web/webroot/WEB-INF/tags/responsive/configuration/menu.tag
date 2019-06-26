<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:message code="sapproductconfig.menu.title" text="Menu" var="menuTitle" />

<div id="configMenu" class="cpq-menu">	
	<div id="configMenuHeader" class="cpq-menu-header">
		<div id="configmenuTitle" class="cpq-menu-title hidden-md hidden-lg">${menuTitle}</div>
		<div id="menuRemoveIcon" class="hidden-md hidden-lg cpq-menu-icon-remove"></div>
	</div>
	<config:menuConflict configData="${config}"/>				
	<c:forEach var="group" items="${config.groups}">
		<config:menuNode group="${group}" level="1" />
	</c:forEach>	
</div>
