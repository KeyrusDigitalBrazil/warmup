<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cssConf" uri="/WEB-INF/tld/addons/ysapproductconfigaddon/sapproductconfig.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="conflict" required="true" type="de.hybris.platform.sap.productconfig.facades.UiGroupData"%>
<%@ attribute name="config" required="true" type="de.hybris.platform.sap.productconfig.facades.ConfigurationData"%>

<div id="menuNode_${conflict.id}"
	class="${cssConf:menuConflictStyleClass(conflict)} <c:if test="${conflict.id eq config.groupIdToDisplay}"> cpq-menu-leaf-selected</c:if>">
	<div id="menuTitle_${conflict.id}" class="cpq-menu-conflict-title" title="${conflict.name}">${conflict.name}</div>
</div>
