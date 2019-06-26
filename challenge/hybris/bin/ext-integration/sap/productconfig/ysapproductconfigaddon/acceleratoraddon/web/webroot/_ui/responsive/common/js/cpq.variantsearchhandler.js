/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
CPQ.variantsearchhandler = {

    getSimilarVariants : function() {
        if ($('#configVariantSearchResults').length > 0) {
            CPQ.core.ajaxRunCounterAsyncServices++;
            if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
                CPQ.core.ajaxStartTimeAsyncServices = new Date().getTime();
            }
            CPQ.config.variantSearchAbortHanlder = function() {
                // firefox just shows "undefined" if we do not cancelled ajax
                // requests proper. Chrome seems to always work fine. IE shows a
                // blank page if we cancel ajax
                if (navigator.userAgent.indexOf("Firefox") > -1) {
                    $.ajax().abort();
                }
            };
            $(window)
                    .bind('beforeunload', CPQ.config.variantSearchAbortHanlder);
            var data = "configId=" + $('#configId').val() + "&productCode="
                    + $(CPQ.core.encodeId('kbKey.productCode')).val();
            if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
                CPQ.core.ajaxServerStartTimeAsyncServices = new Date()
                        .getTime();
            }
            $
                    .post(
                            CPQ.core.getVaraiantSearchUrl(),
                            data,
                            function(response) {
                                if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
                                    CPQ.core.ajaxServerStopTimeAsyncServices = new Date()
                                            .getTime();
                                }
                                CPQ.uihandler.updateSlotContent(response,
                                        "configVariantSearchResults");
                                $(window).unbind('beforeunload',
                                        CPQ.config.variantSearchAbortHanlder);
                                CPQ.config.variantSearchAbortHanlder = undefined;

                                $("#cpqVariantCarousel")
                                        .owlCarousel(
                                                {
                                                    navigation : true,
                                                    navigationText : [
                                                            "<span class='glyphicon glyphicon-chevron-left'></span>",
                                                            "<span class='glyphicon glyphicon-chevron-right'></span>" ],
                                                    pagination : false,
                                                    responsiveBaseWidth : '.cpq-vc-container',
                                                    afterAction : function() {
                                                        CPQ.imagehandler
                                                                .makeLabelsUnderImagesSameHeight(
                                                                        '.cpq-vc-container',
                                                                        '.cpq-vc-name',
                                                                        false);
                                                        CPQ.imagehandler
                                                                .updateVariantImageHeight();
                                                    }
                                                });
                                // register owl eventt handler here
                                CPQ.variantsearchhandler
                                        .registerVariantListOnClickHandlers();
                                CPQ.core.ajaxRunCounterAsyncServices--;
                                if (CPQ.core.ajaxRunCounterAsyncServices === 0) {
                                    CPQ.core.ajaxStopTimeAsyncServices = new Date()
                                            .getTime();
                                }
                            });
        }
    },

    registerVariantListOnClickHandlers : function() {
        $(".cpq-vc-viewDetails-btn").on("click", function(e) {
            CPQ.config.showVariantOverview(e);
        });
    },

    storeVariantSearchState : function() {
        return CPQ.uihandler
                .storeState('#configVariantSearchResults');
    },

    restoreVariantSearchState : function(state) {
        CPQ.uihandler.restoreState(state);
    }

};