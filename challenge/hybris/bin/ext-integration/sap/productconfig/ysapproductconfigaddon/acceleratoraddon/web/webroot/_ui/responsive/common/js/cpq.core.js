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
var CPQ = CPQ || {};

CPQ.core = {
    pageType : "config",
    baseUrl : "",
    ajaxRunning : false,
    ajaxRunCounter : 0,
    ajaxRunCounterAsyncServices: 0,
    ajaxStartTimeAsyncServices: 0,
    ajaxStopTimeAsyncServices: 0,
    ajaxStartTime: 0,
    ajaxStopTime: 0,
    ajaxServerStartTime: 0,
    ajaxServerStopTime: 0,
    ajaxServerStartTimeAsyncServices: 0,
    ajaxServerStopTimeAsyncServices: 0,
    configState: 0,
    formNameId : "#configform",
    waitingMsgTriggeredHandle : "",
    successMsgHideTriggeredHandle : "",

    firePost : function(fx, args) {
        CPQ.core.ajaxRunCounter++;
        if (CPQ.core.ajaxRunCounter === 1) {
            CPQ.core.ajaxStartTime = new Date().getTime();
            CPQ.core.waitingMsgTriggeredHandle = setTimeout(function() {
                CPQ.core.showWaitingMessage();
            }, 500);
        }
        CPQ.core.waitToFirePost(fx, args);
    },

    waitToFirePost : function(fx, args) {
        if (CPQ.core.ajaxRunning === true) {
            setTimeout(function() {
                CPQ.core.waitToFirePost.call(this, fx, args);
            }, 100);
        } else {
            CPQ.core.ajaxRunning = true;
            fx.apply(this, args);
        }
    },

    showWaitingMessage : function() {
        if($(".cpq-engine-state-success").is(":visible")){
            clearTimeout(CPQ.core.successMsgHideTriggeredHandle);
            $(".cpq-engine-state-success").removeAttr('role');
            $(".cpq-engine-state-success").slideUp(300,function() {
                if(CPQ.core.ajaxRunning === true) {
                    $(".cpq-engine-state-running").slideDown(300,function(){
                        $(".cpq-engine-state-running").attr('role','alert');
                    });
                }
            });
        }else{
            $(".cpq-engine-state-running").slideDown(300,function(){
                $(".cpq-engine-state-running").attr('role','alert');
            });
        }
    },

    lastAjaxDone : function() {
        clearTimeout(CPQ.core.waitingMsgTriggeredHandle);
        if ($(".cpq-engine-state-running").is(":visible")) {
            $(".cpq-engine-state-running").removeAttr('role');
            $(".cpq-engine-state-running").slideUp(300,function() {
                $(".cpq-engine-state-success").slideDown(function(){
                    $(".cpq-engine-state-success").attr('role','alert');
                });
                CPQ.core.successMsgHideTriggeredHandle = setTimeout(function() {
                    $(".cpq-engine-state-success").removeAttr('role');
                    $(".cpq-engine-state-success").slideUp(300);
                }, 5000);
            });
        }
        CPQ.core.ajaxStopTime = new Date().getTime();
    },

    // To use any of the meta-characters ( such as
    // !"#$%&'()*+,./:;<=>?@[\]^`{|}~ ) as a literal part of a name, it must be
    // escaped with with two backslashes
    // (https://api.jquery.com/category/selectors/)
    // which is achieved by jQuery3 escapeSelector() function
    // (https://api.jquery.com/jQuery.escapeSelector/)
    encodeId : function(id) {
        var encodedId = "#"+$.escapeSelector(id);
        return encodedId;
    },

    getPageUrl : function() {
        return ACC.config.encodedContextPath + "/cpq/" + this.pageType;
    },

    getVaraiantSearchUrl : function() {
        return ACC.config.encodedContextPath + "/cpq/searchConfigVariant";
    },

    getResetUrl : function() {
        return "reset";
    },

    getAddToCartUrl : function() {
        return "addToCart";
    },

    getAddVariantToCartCleanUpUrl : function() {
        return "addVariantToCartCleanUp";
    },

    getOverviewUrl : function() {
        if (CPQ.core.pageType === "variantOverview") {
            return "variantOverview";
        }
        return "configOverview";
    },

    getPricingUrl : function() {
        return ACC.config.encodedContextPath + "/cpq/updatePricing";
    },

    getAnalyticsUrl : function() {
        return ACC.config.encodedContextPath  + "/cpq/updateAnalytics";
    },

    doRedirect: function(ajaxResponse) {
        var redirectTag = '<div id="redirectUrl">';
        var redirectIndex = ajaxResponse.indexOf(redirectTag);
        if (redirectIndex !== -1) {
            redirectIndex = redirectIndex + redirectTag.length;

            var endTag = "</div>";
            var endIndex = ajaxResponse.indexOf(endTag);
            if (endIndex !== -1) {
                var redirect = ajaxResponse.substring(redirectIndex,
                    endIndex);
                window.location.replace(redirect);
            }
            return true;
        }

        return false;
    },

    actionAndRedirect : function(e, action, url) {
        var input = $('input[name=CSRFToken]');
        var token = null;
        var form = null;

        if (input && input.length !== 0) {
            token = input.attr("value");
        }
        if (token) {
            form = $.parseHTML('<form action="' + action
                    + '" method="post" style="display: none;">'
                    + '<input type="text" name="url" value="' + url + '" />'
                    + '<input type="hidden" name="CSRFToken" value="' + token
                    + '" />' + '</form>');
        } else {
            form = $.parseHTML('<form action="' + action
                    + '" method="post" style="display: none;">'
                    + '<input type="text" name="url" value="' + url + '" />'
                    + '" />' + '</form>');
        }

        $('body').append(form);
        $(form).submit();
    },
    
    prepareAsyncServiceCall : function() {
        CPQ.core.ajaxRunCounterAsyncServices++;
        if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
            CPQ.core.ajaxStartTimeAsyncServices = new Date().getTime();
        }
        var ret = {};
        ret.data = "configId="
               + $('#configId').val();
        ret.configState = CPQ.core.configState;
        if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
            CPQ.core.ajaxServerStartTimeAsyncServices = new Date().getTime();
        }
        return ret;
    }

};
