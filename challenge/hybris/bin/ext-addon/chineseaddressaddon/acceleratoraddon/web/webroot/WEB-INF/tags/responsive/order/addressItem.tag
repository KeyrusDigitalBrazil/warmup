<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="address" required="true" type="de.hybris.platform.commercefacades.user.data.AddressData" %>
<%@ attribute name="storeAddress" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not storeAddress }">
    ${fn:escapeXml(address.fullnameWithTitle)}
    <c:choose>
        <c:when test="${fn:toUpperCase(currentLanguage.getIsocode()) eq 'ZH'}">
			<br>${fn:escapeXml(address.country.name)}&nbsp;
			<c:if test="${not empty address.region.name}">
				${fn:escapeXml(address.region.name)}&nbsp;
			</c:if> 
			<br><c:if test="${not empty address.city.name}">
				${fn:escapeXml(address.city.name)}&nbsp;
			</c:if>
			<c:if test="${empty address.city.name}">
				${fn:escapeXml(address.town)}&nbsp;
			</c:if>
			<c:if test="${not empty address.district.name}">
				${fn:escapeXml(address.district.name)}&nbsp;
			</c:if>
			<br>${fn:escapeXml(address.line1)} 
			<c:if test="${not empty address.line2}">
				<br>${fn:escapeXml(address.line2)}
			</c:if>
			<c:if test="${not empty address.postalCode}">
				<br>${fn:escapeXml(address.postalCode)}
			</c:if>
			<c:if test="${not empty address.cellphone}">
				<br>${fn:escapeXml(address.cellphone)}
			</c:if>
			<c:if test="${not empty address.phone}">
				<br>${fn:escapeXml(address.phone)}
			</c:if>
        </c:when>
        <c:otherwise>
			<br>${fn:escapeXml(address.line1)} 
			<c:if test="${not empty address.line2}">
				<br>${fn:escapeXml(address.line2)}
			</c:if>
			<br><c:if test="${not empty address.district.name}">
				${fn:escapeXml(address.district.name)}&nbsp;
			</c:if>
			<c:if test="${not empty address.city.name}">
				${fn:escapeXml(address.city.name)}&nbsp;
			</c:if>
			<c:if test="${empty address.city.name}">
				${fn:escapeXml(address.town)}&nbsp;
			</c:if>
			<br><c:if test="${not empty address.region.name}">
				${fn:escapeXml(address.region.name)}&nbsp;
			</c:if> 
			${fn:escapeXml(address.country.name)} 
			<c:if test="${not empty address.postalCode}">
				<br>${fn:escapeXml(address.postalCode)}
			</c:if>
			<c:if test="${not empty address.cellphone}">
				<br>${fn:escapeXml(address.cellphone)}
			</c:if>
			<c:if test="${not empty address.phone}">
				<br>${fn:escapeXml(address.phone)}
			</c:if>
        </c:otherwise>
    </c:choose>
</c:if>

<c:if test="${storeAddress }">
	<c:choose>
		<c:when test="${fn:toUpperCase(currentLanguage.getIsocode()) eq 'ZH'}">
		    ${fn:escapeXml(address.country.name)}&nbsp;${fn:escapeXml(address.postalCode)}
			<br/>
		    ${fn:escapeXml(address.region.name)}&nbsp;${fn:escapeXml(address.town)}
			${fn:escapeXml(address.line1)}
		    <c:if test="${not empty address.line2}">
		        <br>
		        ${fn:escapeXml(address.line2)}
		    </c:if>
		    <br>
		    <br>
		    <br/>
		    ${fn:escapeXml(address.phone)}
		</c:when>
		<c:otherwise>
		    ${fn:escapeXml(address.line1)}
		    <c:if test="${not empty address.line2}">
		        <br>
		        ${fn:escapeXml(address.line2)}
		    </c:if>
		    <br>
		    ${fn:escapeXml(address.town)}&nbsp;${fn:escapeXml(address.region.name)}
		    <br>
		    ${fn:escapeXml(address.country.name)}&nbsp;${fn:escapeXml(address.postalCode)}
		    <br/>
		    ${fn:escapeXml(address.phone)}
		</c:otherwise>
	</c:choose>
</c:if>