<%@ taglib prefix="address"
	tagdir="/WEB-INF/tags/addons/chineseaddressaddon/responsive/address"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<span>
<span id="fullName">${fn:escapeXml(deliveryAddress.fullnameWithTitle)}</span>
	<br>
<c:choose>
		<c:when test="${fn:toUpperCase(currentLanguage.getIsocode()) eq 'ZH'}">
			${fn:escapeXml(deliveryAddress.country.name)}
			<c:if test="${not empty deliveryAddress.region.name}">
				,&nbsp;${fn:escapeXml(deliveryAddress.region.name)}
			</c:if>
			<c:if test="${not empty deliveryAddress.city.name}">
				,&nbsp;${fn:escapeXml(deliveryAddress.city.name)}
			</c:if>
			<c:if test="${empty deliveryAddress.city.name}">
				,&nbsp;${fn:escapeXml(deliveryAddress.town)}
			</c:if>
			<c:if test="${not empty deliveryAddress.district.name}">
				,&nbsp;${fn:escapeXml(deliveryAddress.district.name)}
			</c:if>
				,&nbsp;${fn:escapeXml(deliveryAddress.line1)} 
			<c:if test="${not empty deliveryAddress.line2}">
				,&nbsp;${fn:escapeXml(deliveryAddress.line2)}
			</c:if>
			<c:if test="${not empty deliveryAddress.postalCode}">
				,&nbsp;${fn:escapeXml(deliveryAddress.postalCode)}
			</c:if>
			<c:if test="${not empty deliveryAddress.cellphone}">
				,&nbsp;${fn:escapeXml(deliveryAddress.cellphone)}
			</c:if>
			<c:if test="${not empty deliveryAddress.phone}">
				,&nbsp;${fn:escapeXml(deliveryAddress.phone)}
			</c:if>
		</c:when>
		<c:otherwise>
			${fn:escapeXml(deliveryAddress.line1)} 
			<c:if test="${not empty deliveryAddress.line2}">
				,&nbsp;${fn:escapeXml(deliveryAddress.line2)}
			</c:if>
			<c:if test="${not empty deliveryAddress.district.name}">
				,&nbsp;${fn:escapeXml(deliveryAddress.district.name)}
			</c:if>
			<c:if test="${not empty deliveryAddress.city.name}">
				,&nbsp;${fn:escapeXml(deliveryAddress.city.name)}
			</c:if>
			<c:if test="${empty deliveryAddress.city.name}">
				,&nbsp;${fn:escapeXml(deliveryAddress.town)}
			</c:if>
			<c:if test="${not empty deliveryAddress.region.name}">
				,&nbsp;${fn:escapeXml(deliveryAddress.region.name)}
			</c:if> 
			,&nbsp;${fn:escapeXml(deliveryAddress.country.name)} 
			<c:if test="${not empty deliveryAddress.postalCode}">
				,&nbsp;${fn:escapeXml(deliveryAddress.postalCode)}
			</c:if>
			<c:if test="${not empty deliveryAddress.cellphone}">
				,&nbsp;${fn:escapeXml(deliveryAddress.cellphone)}
			</c:if>
			<c:if test="${not empty deliveryAddress.phone}">
				,&nbsp;${fn:escapeXml(deliveryAddress.phone)}
			</c:if>
		</c:otherwise>
	</c:choose>
</span>