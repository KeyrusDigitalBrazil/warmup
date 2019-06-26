<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="csticKey" required="true" type="java.lang.String"%>
<%@ attribute name="typeSuffix" required="true" type="java.lang.String"%>

<c:set var="text" value="${cstic.longText}" />

<label id="${csticKey}.label" for="${csticKey}${typeSuffix}" class="${cssConf:labelStyleClasses(cstic)}">${cstic.langdepname}
	<c:if test="${cstic.required}"><spring:message code="sapproductconfig.legend.required" text="Required" var="legend" /><div title="${legend}" class="cpq-csticlabel-required-icon"></div></c:if><c:if test="${not empty text}"><div class="cpq-csticlabel-longtext-icon"></div></c:if>
	<%-- If a return is added between the DIV tags, an unwanted space appears, so keep everything in one line --%>
</label>
