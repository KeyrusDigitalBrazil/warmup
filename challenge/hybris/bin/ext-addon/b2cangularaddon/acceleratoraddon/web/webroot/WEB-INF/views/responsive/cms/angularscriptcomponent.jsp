<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:htmlEscape defaultHtmlEscape="true" />
 <spring:url value="/" var="contextUrl" />

<%-- Required for Angular routing --%>
<base href="${fn:escapeXml(contextUrl)}" />

<jsp:include page="${file}" />

<script type="text/javascript" src="${fn:escapeXml(contextPath)}/_ui/addons/b2cangularaddon/responsive/common/angular/shim.min.js"></script>
<script type="text/javascript" src="${fn:escapeXml(contextPath)}/_ui/addons/b2cangularaddon/responsive/common/angular/zone.js"></script>
<script type="text/javascript" src="${fn:escapeXml(contextPath)}/_ui/addons/b2cangularaddon/responsive/common/angular/build-${fn:escapeXml(ycommerce:encodeUrl(currentLanguage.isocode))}.js"></script>


