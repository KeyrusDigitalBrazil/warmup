<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spuser" tagdir="/WEB-INF/tags/addons/secureportaladdon/responsive/spuser"%>

<template:page pageTitle="${pageTitle}">
    <jsp:body>
        <div class="register__container">
            <div data-role="content" class="row">
                <div class="col-sm-10">
                    <div class="register__section">
                        <c:url value="/register" var="submitAction" />
                        <spuser:register actionNameKey="register.submit" action="${submitAction}" />
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</template:page>