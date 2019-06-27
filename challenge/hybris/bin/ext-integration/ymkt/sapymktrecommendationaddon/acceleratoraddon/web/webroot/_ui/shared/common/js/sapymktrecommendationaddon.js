/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
(function(global) {

    // global variable to keep track of functions
    var recommendationaddon = {};

    recommendationaddon.oIntervalIds = {};

    function retrieveProductRecommendations(id, sProductCode, sComponentId) {
        var oElement = $("#" + id);
        var sBaseUrl = oElement.data("baseUrl");

        var ajaxUrl = sBaseUrl + '/action/recommendations/';
        $.get(ajaxUrl, {
            id: id,
            productCode: sProductCode,
            componentId: sComponentId
        }, addProductRecommendation(id)).done(function() {

            // we need to check if we are NOT in smartEdit mode && if the
            // component has been initialized.
            if (!bIsInSmartEditMode() && oElement.data("sap-prod-reco-initialized") === true) {
                detectElementFromViewport(oElement, sComponentId, "PRODUCT");
            }

            attachOnClickInteractionClickthrough();

        });
    }

    function attachOnClickInteractionClickthrough() {
        var prodRecoItems = $("body").find("[data-prodreco-item=prodRecoItem]");
        var sBaseUrl = $('[id^="reco"][data-smartedit-component-type="CMSSAPRecommendationComponent"]').data("base-url");

        for (var i = 0; i < prodRecoItems.length; i++) {
            $(prodRecoItems[i]).on("click", function() {

                var itemProductCode = $(this).data("prodreco-item-code");
                var itemProductComponentId = $(this).data("prodreco-item-component-id");

                registerClickthrough(sBaseUrl, itemProductCode, itemProductComponentId);
            });
        }

    }

    function registerClickthrough(baseUrl, productCode, componentId) {
        var ajaxUrl = baseUrl + '/action/prodRecoInteraction/';
        $.post(ajaxUrl, {
            id: productCode,
            componentId: componentId
        }, null);
    }

    function captureImpression(oElement, sComponentId, sImpressionType) {

        if (sImpressionType === "PRODUCT") {
            postImpression(oElement, sComponentId);

        } else if (sImpressionType === "OFFER") {
            postOfferDisplay(oElement, sComponentId);

        }
    }

    function postImpression(oElement, sComponentId) {
        var sBaseUrl = oElement.data("base-url");
        var iItemCount = $(oElement).find("[data-prodreco-item=prodRecoItem]").length;
        var sAjaxUrl = sBaseUrl + '/action/prodRecoImpression/';

        if (iItemCount > 0) {
            $.post(sAjaxUrl, {
                itemCount: iItemCount,
                componentId: sComponentId
            }, null);
        }

    }

    function postOfferDisplay(element, componentId) {

        var baseUrl = element.data("base-url");
        var ajaxUrl = baseUrl + '/action/offerDisplay/';
        var sOfferId = $("#offerRecoUL" + element.attr("id")).data("offerreco-offer-id");
        var sOfferContentId = $("#offerRecoUL" + element.attr("id")).data("offerreco-offer-content-id");

        if (componentId && sOfferId && sOfferContentId) {
            $.post(ajaxUrl, {
                componentId: componentId,
                offerid: sOfferId,
                offerContentId: sOfferContentId
            }, null);
        }
    }

    function detectElementFromViewport(element, componentId, impressionType) {

        /**
         * This prototype function checks if the element is on the current user screen
         */
        $.fn.bElementIsViewedByUser = function(element) {

            var win = $(window);

            // gets the user browser screen dimension in pixel
            var viewport = {
                top: win.scrollTop(),
                left: win.scrollLeft()
            };
            viewport.right = viewport.left + win.width();
            viewport.bottom = viewport.top + win.height();

            var elementBounds = element.offset();
            elementBounds.right = elementBounds.left + element.outerWidth();
            elementBounds.bottom = elementBounds.top + element.outerHeight();

            // Check if one pixel of element is visible
            return (viewport.right >= elementBounds.left && //
                viewport.left <= elementBounds.right && //
                viewport.bottom >= elementBounds.top && viewport.top <= elementBounds.bottom);
        };

        if (!recommendationaddon.oIntervalIds.hasOwnProperty(element.attr("id"))) {
            recommendationaddon.oIntervalIds[element.attr("id")] = setInterval(checkVisibility, 500, element, componentId, impressionType);
        }

        // this function checks if the input element is on the screen
        // periodically
        function checkVisibility(oElement, componentId, impressionType) {
            try {
                if ($.prototype.bElementIsViewedByUser(oElement)) {
                    clearInterval(recommendationaddon.oIntervalIds[oElement.attr("id")]);
                    captureImpression(oElement, componentId, impressionType);
                }
            } catch (e) {
                console.log(e);
                clearInterval(recommendationaddon.oIntervalIds[oElement.attr("id")]);
            }
        }
    }

    /* This is the function that creates the component */
    function loadProductRecommendations() {
        var divs = $('[id^="reco"][data-smartedit-component-type="CMSSAPRecommendationComponent"]');

        for (var i = 0; i < divs.length; i++) {
            if (divs[i].id.search("reco") > -1 && !($(divs[i]).data("sap-prod-reco-initialized") === true)) {
                var sProductCode = $("#" + divs[i].id).data("prodcode");
                var sComponentId = $("#" + divs[i].id).data("componentid");

                retrieveProductRecommendations(divs[i].id, sProductCode, sComponentId);
                $(divs[i]).data("sap-prod-reco-initialized", true);
            }
        }
    }

    function addProductRecommendation(recoId) {
        return function(data) {
            var $recoComponent = $("#" + recoId);

            if (data !== '') {
                $recoComponent.append(data);
                $recoComponent.addClass('sap-reco-initialized');

                try {
                    $('#prodRecoUL' + recoId).owlCarousel(ACC.carousel.carouselConfig.default);
                } catch (e) {
                    console.error('Failed to apply owlCarousel styling', e);
                }


            } else {
                $recoComponent.hide();
            }
        };
    }

    function loadOfferRecommendations() {
        var divs = $('[id^="reco"][data-smartedit-component-type="CMSSAPOfferRecoComponent"]');

        for (var i = 0; i < divs.length && !($(divs[i]).data("sap-offer-reco-initialized") === true); i++) {
            var sComponentId = $("#" + divs[i].id).data("componentid");
            retrieveOfferRecommendations(divs[i].id, sComponentId);
            $(divs[i]).data("sap-offer-reco-initialized", true);
        }
    }

    function retrieveOfferRecommendations(id, sComponentId) {
        var oElement = $("#" + id);
        var sComponentDataId = oElement.data("componentid");
        var baseUrl = oElement.data("base-url");
        var ajaxUrl = baseUrl + '/action/offers/';
        $.get(ajaxUrl, {
            id: id,
            componentId: sComponentDataId
        }, addOffer(id)).done(function() {

            // we need to check if we are NOT in smartEdit mode && if the
            // component has been initialized.
            if (!bIsInSmartEditMode() && oElement.data("sap-offer-reco-initialized") === true) {
                detectElementFromViewport(oElement, sComponentDataId, "OFFER");
            }

            attachOnClickOfferClickInteraction();
        });

    }

    function attachOnClickOfferClickInteraction() {
        var oOfferRecoItems = $("body").find("[data-offerreco-item=offerRecoItem]");
        var sBaseUrl = $('[id^="reco"][data-smartedit-component-type="CMSSAPOfferRecoComponent"]').data("base-url");

        for (var i = 0; i < oOfferRecoItems.length; i++) {
            $(oOfferRecoItems[i]).on("click", function() {

                var sOfferId = $(this).data("offerreco-offer-id");
                var sOfferContentId = $(this).data("offerreco-offer-content-id");
                var sComponentId = $(this).data("offerreco-offer-component-id");

                postOfferClickInteraction(sBaseUrl, sOfferId, sOfferContentId, sComponentId);
            });
        }

    }

    function postOfferClickInteraction(sBaseUrl, sOfferId, sOfferContentId, sComponentId) {

        var baseUrl = $('[id^="reco"][data-smartedit-component-type="CMSSAPOfferRecoComponent"]').data("base-url");

        var ajaxUrl = baseUrl + '/action/offerClick/';
        $.post(ajaxUrl, {
            componentId: sComponentId,
            offerid: sOfferId,
            offerContentId: sOfferContentId
        }, null);
    }



    function addOffer(offerRecoId) {
        return function(data) {

            var $OfferComponentContainer = $("#" + offerRecoId);

            if (data !== '') {
                $OfferComponentContainer.append(data);
                $OfferComponentContainer.addClass('sap-reco-initialized');
            } else {
                if (!bIsInSmartEditMode()) {
                    $OfferComponentContainer.hide();
                }

            }
        };
    }

    function bIsInSmartEditMode() {
        var urlPathName = window.parent.location.pathname;
        if (urlPathName === "/smartedit/") {
            return true;
        }
        return false;
    }

    function bIsSmartEditEnabled() {
        if (typeof window.smartedit != 'undefined' && typeof window.smartedit.addOnReprocessPageListener === "function") {
            return true;
        }
        return false;
    }

    $(function() {

        // add our function to the SmartEdit reprocessPage
        if (bIsSmartEditEnabled()) {
            window.smartedit.addOnReprocessPageListener(loadOfferRecommendations);
            window.smartedit.addOnReprocessPageListener(loadProductRecommendations);
            window.smartedit.reprocessPage();
        } else {
            // SmartEdit undefined, therefore trigger recommendation retrieval for
            // initial page load
            loadOfferRecommendations();
            loadProductRecommendations();
        }
    });




})(this);