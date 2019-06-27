<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true"/>
<script type="text/javascript" src="${contextPath}/_ui/addons/chineseproductsharingaddon/responsive/common/js/jquery.qrcode.min.js"></script>
<p style="margin-left:12%"><spring:theme code="type.ChineseProductSharingComponent.description" /></p>
<div id="qrcodeCanvas" style="margin-left:15%"></div>

<script type="text/javascript">
jQuery('#qrcodeCanvas').qrcode({
	width   : 200,
	height  : 200,
	text	: "${fn:escapeXml(url)}"
});	
</script>
