<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<div class="slider_component simple-banner">
	<div id="homepage_slider">
		<c:forEach items="${banners}" var="banner">
        	<cms:component component="${banner}" evaluateRestriction="true" />
		</c:forEach>
	</div>
</div>
