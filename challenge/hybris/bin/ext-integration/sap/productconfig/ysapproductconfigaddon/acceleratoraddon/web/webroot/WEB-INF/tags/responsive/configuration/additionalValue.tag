<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="csticKey" required="true" type="java.lang.String"%>
<%@ attribute name="pathPrefix" required="true" type="java.lang.String"%>

<div class="cpq-additionalValue ${cssConf:valueStyleClass(cstic)} col-xs-12 col-sm-12 ">
	<c:choose>
		<c:when test="${cstic.maxlength > 0}">
			<form:input id="${csticKey}.input" class="form-control" type="text" path="${pathPrefix}additionalValue"
				maxlength="${cstic.maxlength}" />
		</c:when>
		<c:otherwise>
			<form:input id="${csticKey}.input" class="form-control" type="text" path="${pathPrefix}additionalValue" />
		</c:otherwise>
	</c:choose>
</div>

