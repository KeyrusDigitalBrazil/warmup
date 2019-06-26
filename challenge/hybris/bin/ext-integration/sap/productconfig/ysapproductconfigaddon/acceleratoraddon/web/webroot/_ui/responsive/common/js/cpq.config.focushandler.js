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

CPQ.focushandler = {
    focusOnFirstInput : function() {
        var csticKey = $(".cpq-groups label").attr("id");
        if (csticKey) {
            csticKey = csticKey.substring(0, csticKey.length - 6);

            var offset = $(".cpq-groups label").offset().top
                    - $(".cpq-groups").offset().top;
            CPQ.focushandler.focusOnInputByCsticKey(csticKey, offset);
        }
    },

    focusOnInputByCsticKey : function(csticKey, additionalOffset) {
        var directNodeList = $(CPQ.core.encodeId(csticKey));
        if (directNodeList.length === 1) {
            CPQ.focushandler.focusSetEnsure(directNodeList, 0);
            return;
        }

        var csticLabelId = csticKey + ".label";
        var csticKeyId = $(CPQ.core.encodeId(csticKey + ".key"));
        var csticValueName = csticKeyId.attr("name");
        // replace .key with .value
        csticValueName = csticValueName.substring(0, csticValueName.length - 4);
        csticValueName = csticValueName + ".value";

        // focus on Input if existent
        var nodeList = $("[name='" + csticValueName + "']");
        var length = nodeList.length;

        var focusElem;
        if (length > 0) {
            // length == 1: a string input field / DDLB ==> focus on it
            // length > 1: radio buttons ==> focus on first element           
            var checkedElem = nodeList.filter(':checked');
            if(checkedElem.length > 0){
                focusElem = checkedElem.first();
            }else{
                // style class for DDLB -> parent and search for menu widget style
                focusElem = nodeList.first();
                if(focusElem.hasClass('cpq-ddlb')){
                    focusElem = focusElem.parent().find(".ui-selectmenu-button");
                }
            }
        } else {
            // if length = 0
            // maybe a numeric input field?
            var numericInputFieldName = csticValueName.substring(0, csticValueName.length - 6);
            numericInputFieldName += ".formattedValue";
            nodeList = $("[name='" + numericInputFieldName + "']");
            length = nodeList.length;
            if (length > 0){
                focusElem = nodeList.first();
            }
            else {
                // maybe a check box List?
                var firstCheckboxName = csticValueName.slice(0,
                        csticValueName.length - 5);
                firstCheckboxName += "domainvalues[0].selected";
                nodeList = $("[name='" + firstCheckboxName + "']").filter(':visible');
                length = nodeList.length;
                // focus on first checkbox
                if (length > 0) {
                    // restrict jquery search scope as checkboxValues might not
                    // be unique
                    focusElem = $(nodeList[0], csticLabelId);
                } else {
                    // no input at all, could be a read only field,
                    // retract value?
                    var retractValue =  $(CPQ.core.encodeId(csticKey + ".readOnly")).next().filter('.cpq-conflict-retractValue').find('a');
                    if(retractValue.length > 0){
                        focusElem = retractValue.first();
                    }else{
                        // focus on Label instead
                        focusElem = $(CPQ.core.encodeId(csticLabelId));
                    }
                }
            }
        }

        var scrollToElem;
        var label = $(CPQ.core.encodeId(csticLabelId)).first();
        var conflictsOffset = 0;
        if (!additionalOffset) {
            additionalOffset = 0;
        }

        if ('conflict' === csticKey.substring(0,8)) {
            // scroll to conflict header
            scrollToElem = label.parents('.cpq-group').prev();
            additionalOffset = 0;
            conflictsOffset = 15;
        } else {
            // scroll to label
            scrollToElem = label;
        }

        var offset = additionalOffset + conflictsOffset + focusElem.offset().top - scrollToElem.offset().top;
        CPQ.focushandler.focusRestoreOnElement(focusElem, true, offset);
    },

    focusRestore : function(focusElementId, srollTo, additionalOffset) {
        if (focusElementId && focusElementId.length > 0) {
            var focusElement = $(CPQ.core.encodeId(focusElementId));
            CPQ.focushandler.focusRestoreOnElement(focusElement, srollTo,
                    additionalOffset);
        }
    },

    focusSave : function() {
        var id = undefined;
        if (document.activeElement) {
            id = document.activeElement.id;
        }
        return id;
    },

    focusRestoreOnElement : function(focusElement, srollTo, additionalOffset) {
        if (focusElement[0]) {
            var actualScrollTop = $(window).scrollTop();
            CPQ.focushandler.focusSetEnsure(focusElement, 0);
            if (srollTo) {
                var offset = focusElement.offset().top;
                var stickyBanner = $(".stickyBranding");
                var stickyBannerHeight = 0;
                if (stickyBanner.is(":visible")) {
                    stickyBannerHeight = stickyBanner.outerHeight();
                }
                if (!additionalOffset) {
                    additionalOffset = 0;
                }
                offset = offset - stickyBannerHeight - additionalOffset;
                $("html, body").animate({
                    scrollTop : offset
                }, 100);
            }else if(additionalOffset && additionalOffset !== 0){
                var newScrollTop = actualScrollTop + additionalOffset;
                $(window).scrollTop(newScrollTop);
            }
        }
    },

    focusSetEnsure : function(focusElement, counter) {
        focusElement.focus();
        if (!focusElement.is(":focus") && counter < 20) {
            counter++;
            setTimeout(function() {
                counter++;
                CPQ.focushandler.focusSetEnsure(focusElement, counter);
            }, 50);
        }
    },

    hoverOrFocusOnValueImage: function (elem) {
        if (CPQ.config.valueChangeViaImage) {
            CPQ.config.valueChangeViaImage = false;
        } else {
            elem.addClass("cpq-cstic-value-image-container-hover");
        }
    },

    hoverLostOrBlurOnVlaueImage: function (elem) {
        elem.removeClass("cpq-cstic-value-image-container-hover");
        CPQ.config.valueChangeViaImage = false;
    }
};
