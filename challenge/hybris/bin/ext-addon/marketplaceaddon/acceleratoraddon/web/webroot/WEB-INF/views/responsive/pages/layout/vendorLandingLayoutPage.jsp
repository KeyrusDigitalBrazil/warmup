<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/addons/marketplaceaddon/responsive/product"%>

<template:page pageTitle="${pageTitle}">
	<div class="row">
			<div  class="col-sm-3 vendorLeft">
				<cms:pageSlot position="VendorLeftBodySlot" var="feature" element="div" class="product-list-left-refinements-slot">
					<cms:component component="${feature}" element="div" class="product-list-left-refinements-component yComponentWrapper" />
				</cms:pageSlot>
			</div>

			<div  class="col-md-9 vendorLandingSlot">
				  <div class="hidden-md hidden-lg refine-bar">
					<product:productRefineButton styleClass="col-xs-12 btn btn-default pull-right js-show-facets vendorRefine"/>
				</div> 
				<div class="no-space">
					<cms:pageSlot position="Section2A" var="feature" element="div" class="no-margin">
						<cms:component component="${feature}" element="div" class="col-xs-12 no-space yComponentWrapper" />
					</cms:pageSlot>
				</div>

				<div class="no-space">
					<cms:pageSlot position="Section3" var="feature" element="div" class="landingLayout2PageSection2C">
						<cms:component component="${feature}" element="div" class="col-xs-12 no-space yComponentWrapper" />
					</cms:pageSlot>
				</div>
			</div>
	</div>
</template:page>
