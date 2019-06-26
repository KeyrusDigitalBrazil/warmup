<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:forEach items="${medias}" var="media">
	<c:choose>
		<c:when test="${empty imagerData}">
			<c:set var="imagerData">"${ycommerce:encodeJSON(media.width)}":"${ycommerce:encodeJSON(media.url)}"</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="imagerData">${imagerData},"${ycommerce:encodeJSON(media.width)}":"${ycommerce:encodeJSON(media.url)}"</c:set>
		</c:otherwise>
	</c:choose>
	<c:if test="${empty altText}">
		<c:set var="altTextHtml" value="${media.altText}"/>
	</c:if>
</c:forEach>

<c:set var="imagerDataJson" value="{${imagerData}}"/>

<c:url value="${urlLink}" var="encodedUrl" />
<div class="banner banner__component--responsive">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
			<img class="js-responsive-image"  data-media="${fn:escapeXml(imagerDataJson)}" alt='${fn:escapeXml(altTextHtml)}' title='${fn:escapeXml(altTextHtml)}' style="">
		</c:when>
		<c:otherwise>
			<a href="${fn:escapeXml(encodedUrl)}">
				<img class="js-responsive-image"  data-media="${fn:escapeXml(imagerDataJson)}" title='${fn:escapeXml(altTextHtml)}' alt='${fn:escapeXml(altTextHtml)}' style="">
			</a>
		</c:otherwise>
	</c:choose>
</div>