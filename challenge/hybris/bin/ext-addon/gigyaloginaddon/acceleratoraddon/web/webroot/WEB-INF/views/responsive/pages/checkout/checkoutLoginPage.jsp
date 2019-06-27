<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<template:page pageTitle="${pageTitle}">
	
	<div class="account-section">
            <cms:pageSlot position="BodyContent" var="feature" element="div" class="account-section-content">
                <cms:component component="${feature}" />
            </cms:pageSlot>
        </div>

        <cms:pageSlot position="BottomContent" var="feature" element="div" class="accountPageBottomContent">
            <cms:component component="${feature}" />
        </cms:pageSlot>
        
</template:page>