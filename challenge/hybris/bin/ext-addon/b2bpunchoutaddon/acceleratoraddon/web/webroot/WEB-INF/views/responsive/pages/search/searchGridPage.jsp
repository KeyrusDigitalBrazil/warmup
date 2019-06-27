<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/b2bpunchoutaddon/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<template:page pageTitle="${pageTitle}">

    <div class="row">
        <div class="col-xs-3">
            <cms:pageSlot position="ProductLeftRefinements" var="feature" element="div" class="search-grid-page-left-refinements-slot">
                <cms:component component="${feature}" element="div" class="search-grid-page-left-refinements-component"/>
            </cms:pageSlot>
        </div>
        <div class="col-sm-12 col-md-9">
            <cms:pageSlot position="SearchResultsGridSlot" var="feature" element="div" class="search-grid-page-result-grid-slot">
                <cms:component component="${feature}" element="div" class="search-grid-page-result-grid-component"/>
            </cms:pageSlot>
        </div>
    </div>

</template:page>
