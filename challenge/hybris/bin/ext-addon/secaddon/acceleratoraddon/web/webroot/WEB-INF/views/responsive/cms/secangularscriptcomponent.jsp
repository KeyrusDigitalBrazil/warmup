<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<base href="${encodedContextPath}/">
<jsp:include page="${file}" />
<script type="text/javascript" src="${contextPath}/_ui/addons/secaddon/responsive/common/angular/shim.min.js"></script>
<script type="text/javascript" src="${contextPath}/_ui/addons/secaddon/responsive/common/angular/zone.js"></script>
<script type="text/javascript" src="${contextPath}/_ui/addons/secaddon/responsive/common/angular/build-${ycommerce:encodeUrl(currentLanguage.isocode)}.js"></script>



