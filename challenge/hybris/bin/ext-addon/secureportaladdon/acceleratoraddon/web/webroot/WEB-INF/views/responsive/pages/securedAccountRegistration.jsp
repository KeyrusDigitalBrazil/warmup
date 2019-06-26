<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sptemplate" tagdir="/WEB-INF/tags/addons/secureportaladdon/responsive/sptemplate"%>
<%@ taglib prefix="spuser" tagdir="/WEB-INF/tags/addons/secureportaladdon/responsive/spuser"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<sptemplate:page pageTitle="${pageTitle}">
	<jsp:body>
        <div class="register__container">
            <div class="row" data-role="content">
                <div class="col-sm-10">
                    <div class="register__section">
                        <c:url value="/register" var="submitAction" />
                        <spuser:register actionNameKey="register.submit" action="${submitAction}" />
                    </div>
                </div>

                <div class="col-sm-2">
                    <div class="item_container">
                        <cms:pageSlot position="SideContent" var="feature" element="div" class="side-content-slot cms_disp-img_slot">
                            <cms:component component="${feature}"/>
                        </cms:pageSlot>
                    </div>
                </div>
            </div>
        </div>
	</jsp:body>
</sptemplate:page>
