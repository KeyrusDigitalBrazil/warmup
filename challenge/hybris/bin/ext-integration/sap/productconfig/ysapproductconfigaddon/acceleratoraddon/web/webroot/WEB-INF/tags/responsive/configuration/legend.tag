<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>

<div class="cpq-legend-row">
	<spring:message code="sapproductconfig.legend.required" text="Required" var="legend" />
	<div class="cpq-legend"><div class="cpq-csticlabel-required-icon"></div>${legend}</div>
</div>
