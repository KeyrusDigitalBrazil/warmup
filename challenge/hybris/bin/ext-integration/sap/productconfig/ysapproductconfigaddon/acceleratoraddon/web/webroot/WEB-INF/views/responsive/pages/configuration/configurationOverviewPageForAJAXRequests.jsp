<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>


<div id="start:overviewContentSlot" />
	<cms:pageSlot id="overviewContentSlot" position="ConfigOverviewContent" var="feature" element="div">
		<cms:component component="${feature}" />
	</cms:pageSlot>
<div id="end:overviewContentSlot" />

<div id="start:overviewSidebarSlot" />
	<cms:pageSlot id="overviewSidebarSlot" position="ConfigOverviewSidebar" var="feature" element="div">
		<cms:component component="${feature}" />
	</cms:pageSlot>
<div id="end:overviewSidebarSlot" />
	