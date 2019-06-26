<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="config" tagdir="/WEB-INF/tags/addons/ysapproductconfigaddon/responsive/configuration"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>

<%@ attribute name="cstic" required="true" type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="groupType" required="true" type="de.hybris.platform.sap.productconfig.facades.GroupType"%>

<%@ attribute name="suggNo" required="true" type="java.lang.Integer"%>
<%@ attribute name="noOfsugg" required="true" type="java.lang.Integer"%>

<c:if test="${groupType eq 'CONFLICT' and noOfsugg > 1}">
	<div class="cpq-suggestion-box" title="Change value for ${cstic.langdepname}">
		<div id="suggestionTitle" class="cpq-suggestion-title">
			<div class="cpq-suggestion-title-prefix"> <spring:message code="sapproductconfig.conflict.suggestion"
					text="Suggestion {0}" arguments="${suggNo}" /></div>
			<div class="cpq-suggestion-title-suffix"><spring:message code="sapproductconfig.conflict.suggestion.change.value" text="Change value for {0}"
				arguments="${cstic.langdepname}" />
			</div>
		</div>
	</div>
</c:if>
