<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>

<c:if test="${not empty config.focusId}">
	<div id="cpqFocusOnElement">${config.focusId}</div>
</c:if>

<div id="start:cpq-message-area" />
<div id="cpq-message-area">
	<common:globalMessages />
</div>
<div id="end:cpq-message-area" />

<div id="start:configContentSlot" />
<cms:pageSlot id="configContentSlot" position="ConfigContent" var="feature" element="div"
	class="span-16 configContent cms_disp-img_slot">
	<cms:component component="${feature}" />
</cms:pageSlot>
<div id="end:configContentSlot" />

<div id="start:configSidebarSlot" />
<cms:pageSlot id="configSidebarSlot" position="ConfigSidebar" var="feature" element="div">
	<cms:component component="${feature}" />
</cms:pageSlot>
<div id="end:configSidebarSlot" />

<div id="start:configBottombarSlot" />
<cms:pageSlot id="configBottombarSlot" position="ConfigBottombar" var="feature" element="div">
	<cms:component component="${feature}" />
</cms:pageSlot>
<div id="end:configBottombarSlot" />
