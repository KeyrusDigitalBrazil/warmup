<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/responsive/template" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<template:page pageTitle="${pageTitle}">
	<jsp:attribute name="pageScripts">
	<script  type="text/javascript">
		/*<![CDATA[*/
		$(document).ready(function() {
			$('#procurementForm').submit();
		});
		/*]]>*/
		</script>
	</jsp:attribute>
	<jsp:body>
	<form id="procurementForm" id="procurementForm" method="post" action="${fn:escapeXml(browseFormPostUrl)}">
		<input type="hidden" name="cxml-base64" value="${fn:escapeXml(orderAsCXML)}">
	</form>
	</jsp:body>
</template:page>
