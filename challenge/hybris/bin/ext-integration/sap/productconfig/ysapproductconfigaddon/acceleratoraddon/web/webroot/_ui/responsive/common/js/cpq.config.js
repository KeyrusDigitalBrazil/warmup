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

CPQ.config = {
    lastTarget: undefined,
    addToCartClickTimestamp: 0,
    bindAll: function () {
        if (!String.prototype.startsWith) {
            String.prototype.startsWith = function (searchString, position) {
                position = position || 0;
                return this.indexOf(searchString, position) === position;
            };
        }
        CPQ.analyticshandler.init();
        CPQ.variantsearchhandler.getSimilarVariants();
        CPQ.pricinghandler.getPricing();
        CPQ.analyticshandler.getAnalytics();
        CPQ.ddlbhandler.cpqDdlb();
        CPQ.config.registerStaticOnClickHandlers();
        CPQ.config.doAfterPost();
        CPQ.config.generateAriaDescribedBy();

        $(window).resize(
            function () {
                CPQ.imagehandler.makeLabelsUnderImagesSameHeight(
                    '.cpq-csticValue', '.cpq-csticValueImageLabel',
                    true);
            });

    },

    registerStaticOnClickHandlers: function () {
        $(".product-details .name").on(
            "click keypress",
            function (e) {
                CPQ.imagehandler.clickHideShowImageGallery(
                    CPQ.config.doUpdatePost, e);
            });
        $("#cpqMenuArea").on("click", function (e) {
            CPQ.menuhandler.menuOpen(e);
        });
    },

    registerOnClickHandlers: function () {
        $(".cpq-group-title-close, .cpq-group-title-open").on("click keydown",
            function (e) {
                CPQ.uihandler.clickGroupHeader(CPQ.config.doUpdatePost, e);
            });

        // mousedown/mouseup replaces the "click" event registration
        // If the user changes a value in a text field and clicks direct
        // addToCart, the page will be updated by the onChange event
        // and the "old" button receives the "mousedown" and the "new"
        // button receives the "mouseup" event - no "click" event
        // (click is mousedown+mouseup in one "field").
        // Now with the mousedown the time is taken and with the mousup it is
        // checked if less than 2 seconds are passed by, since mousedown.
        $(".cpq-btn-addToCart").on("mousedown", function (e) {
            CPQ.config.addToCartClickTimestamp = e.timeStamp;
        });

        $(".cpq-btn-addToCart").on("mouseup", function (e) {
            if ((e.timeStamp - CPQ.config.addToCartClickTimestamp) < 2000) {
                CPQ.config.clickAddToCartButton();
            }
        });

        $(".cpq-btn-addToCart").on("keydown", function (e) {
            if (e.which === 13) {
                CPQ.config.clickAddToCartButton();
            }
        });

        $(".cpq-csticlabel-longtext-icon").on('click', function (e) {
            CPQ.config.longTextIconClicked(e);
        });

        $(".cpq-promo-message-link").on('click', function (e) {
            CPQ.config.toggleMessageDetails(e);
        });

        $(".cpq-promo-message-link").on("keydown", function (e) {
            if (e.which === 13) {
                CPQ.config.toggleMessageDetails(e);
            }
        });

        CPQ.config.registerConflictOnClickHandlers();
        CPQ.config.registerMenuOnClickHandlers();
        CPQ.config.registerPreviousNextOnClickHandlers();
        CPQ.config.registerCsticValueImageOnClickHandler();
        CPQ.analyticshandler.registerAnalyticsTooltip();
    },

    registerConflictOnClickHandlers: function () {
        $(".cpq-conflict-link-to-config").on(
            "click",
            function (e) {
                CPQ.config.handleConflictNavigation(
                    CPQ.idhandler.getCsticIdFromCsticFieldId,
                    "NAV_TO_CSTIC_IN_GROUP", e);
            });

        $(".cpq-conflict-link").on(
            "click",
            function (e) {
                CPQ.config.handleConflictNavigation(
                    CPQ.idhandler.getCsticIdFromViolatedCsticFieldId,
                    "NAV_TO_CSTIC_IN_CONFLICT", e);
            });

        $(".cpq-conflict-retractValue-button").on("click", function (e) {
            CPQ.config.handleRetractConflict(e);
        });
    },

    registerMenuOnClickHandlers: function () {
        $(".cpq-menu-node, .cpq-menu-conflict-header").on("click", function (e) {
            CPQ.menuhandler.menuGroupToggle(e);
        });
        $(".cpq-menu-leaf, .cpq-menu-conflict-node").on("click", function (e) {
            CPQ.menuhandler.menuNavigation(e);
        });
        $(".cpq-menu-icon-remove").on("click", function (e) {
            CPQ.menuhandler.menuClose(e);
        });
    },

    registerPreviousNextOnClickHandlers: function () {
        $(".cpq-previous-button").on("click", function (e) {
            CPQ.config.previousNextButtonClicked("PREV_BTN", e);
        });
        $(".cpq-next-button").on("click", function (e) {
            CPQ.config.previousNextButtonClicked("NEXT_BTN", e);
        });
    },

    registerCsticValueImageOnClickHandler: function () {
        var multiSelectValueImages = $(".cpq-cstic-value-container-multi");
        multiSelectValueImages.on("click", function (e) {
            CPQ.imagehandler.csticValueImageMultiClicked(e);
        });

        multiSelectValueImages.on("keypress", function (e) {
            if (e.which === 13 || e.which === 32) { // enter or space
                CPQ.imagehandler.csticValueImageMultiClicked(e);
            }
        });

        var singleSelectValueImages = $(".cpq-cstic-value-container-single");
        singleSelectValueImages.on("click", function (e) {
            CPQ.imagehandler.csticValueImageSingleClicked(e);
        });

        singleSelectValueImages.on("keypress", function (e) {
            if (e.which === 13 || e.which === 32) { // enter or space
                CPQ.imagehandler.csticValueImageSingleClicked(e);
            }
        });

        var valueImageContainer = $(".cpq-cstic-value-image-container");
        valueImageContainer.on("mouseenter", function (e) {
            CPQ.focushandler.hoverOrFocusOnValueImage($(this));
        });
        valueImageContainer.on("focusin", function (e) {
            CPQ.focushandler.hoverOrFocusOnValueImage($(this));
        });
        valueImageContainer.on("mouseleave", function (e) {
            CPQ.focushandler.hoverLostOrBlurOnVlaueImage($(this));
        });
        valueImageContainer.on("focusout", function (e) {
            CPQ.focushandler.hoverLostOrBlurOnVlaueImage($(this));
        });
    },

    longTextIconClicked: function (e) {
        var labelId = $(e.currentTarget).parent().attr('id');
        var csticId = CPQ.idhandler.getCsticIdFromLableId(labelId);
        var targetId = CPQ.core.encodeId(csticId + ".showFullLongText");
        var cpqAction;
        if ($(targetId).val() !== "true") {
            $(targetId).val("true");
            cpqAction = 'SHOW_FULL_LONG_TEXT';
            $(e.currentTarget).next().show();
        } else {
            $(targetId).val("false");
            cpqAction = 'HIDE_FULL_LONG_TEXT';
            $(e.currentTarget).next().hide();
        }
        csticId = CPQ.idhandler.getCsticIdFromConflictCstic(csticId);

        var data = CPQ.config
            .getSerializedConfigForm(cpqAction, csticId, false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    toggleMessageDetails: function (e) {
        var linkId = $(e.currentTarget).attr('id');
        var csticId = CPQ.idhandler.getCsticIdFromMessageId(linkId);
        var targetId = CPQ.core.encodeId(csticId + ".showExtendedMessage");
        if ($(targetId).val() !== "true") {
            $(e.currentTarget).hide();
            $(e.currentTarget).next().show();
        } else {
            var extMessageElement = $(e.currentTarget).parent();
            extMessageElement.hide();
            extMessageElement.prev().show();
        }

        var actionData = linkId + ";" + $(e.currentTarget).attr('data-target');
        var data = CPQ.config.getSerializedConfigForm(
            'TOGGLE_EXTENDED_MESSAGE', actionData, false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    previousNextButtonClicked: function (cpqAction, e) {
        $("#autoExpand").val(false);
        var data = CPQ.config.getSerializedConfigForm(cpqAction, "", false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data, "##first##"]);
    },

    handleConflictNavigation: function (getCsticIdFx, cpqAction, e) {
        var csticFieldId = $(e.currentTarget).parent().attr("id");
        var csticId = getCsticIdFx.apply(this, [csticFieldId]);
        $("#autoExpand").val(false);
        var data = CPQ.config.getSerializedConfigForm(cpqAction, csticId, true,
            "");
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    handleRetractConflict: function (e) {
        var csticFieldId = $(e.currentTarget).parent().attr("id");
        var csticId = CPQ.idhandler.getCsticIdFromCsticFieldId(csticFieldId);
        var targetId = CPQ.core.encodeId("conflict." + csticId
            + ".retractValue");
        var path = $(e.currentTarget).parents(".cpq-cstic").children(
            "input:hidden").attr("name");
        $(targetId).val(true);
        var data = CPQ.config.getSerializedConfigForm("RETRACT_VALUE", path,
            false, "");
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    ensureMessagesAreVisible: function () {
        if ($(".alert-info").length && !conflicts) {
            $(".alert-info").each(function () {
                if ($(this).parent().attr("class") === "global-alerts") {
                    $(document).scrollTop($(".alert-info").offset().top - 10);
                    return false;
                }
            });
        }
    },

    doAfterPost: function () {
        CPQ.config.registerOnClickHandlers();
        CPQ.config.registerAjax();
        if ($("#focusId").attr("value").length > 0) {
            CPQ.focushandler
                .focusOnInputByCsticKey($("#focusId").attr("value"));
            $("#focusId").val("");
        }
        CPQ.config.ensureMessagesAreVisible();
        CPQ.imagehandler.makeLabelsUnderImagesSameHeight('.cpq-csticValue',
            '.cpq-csticValueImageLabel', true);
        CPQ.config.ifExpModeActive();
    },

    showVariantOverview: function (e) {
        var sUrl = $(e.currentTarget).data("goToOverview");
        window.location = sUrl;
    },

    doUpdatePost: function (e, data, focusId) {
        CPQ.core.configState++;
        CPQ.core.ajaxServerStartTime = new Date().getTime();
        $.post(CPQ.core.getPageUrl(), data, function (response) {
            if (CPQ.core.ajaxRunCounter === 1) {
                CPQ.core.ajaxServerStopTime = new Date().getTime();
                if (CPQ.core.doRedirect(response)) {
                    return;
                }
                var focusElementId;
                var scrollTo;
                var oldOffsetTop = 0;
                if (focusId) {
                    focusElementId = focusId;
                    scrollTo = true;
                } else {
                    focusElementId = CPQ.focushandler.focusSave();
                    scrollTo = false;
                    oldOffsetTop = CPQ.config.getOffset(focusElementId);
                }
                CPQ.config.updateContent(response);
                CPQ.ddlbhandler.cpqDdlb();
                CPQ.variantsearchhandler.getSimilarVariants();
                CPQ.pricinghandler.getPricing();
                CPQ.analyticshandler.getAnalytics();
                CPQ.config.generateAriaDescribedBy();
                if (focusElementId === "##first##") {
                    CPQ.focushandler.focusOnFirstInput();
                } else {
                    CPQ.config.doScrolling(focusElementId, scrollTo,
                        oldOffsetTop);
                }
                CPQ.config.doAfterPost();
                CPQ.core.lastAjaxDone();
            }
            CPQ.core.ajaxRunning = false;
            CPQ.core.ajaxRunCounter--;
        });
        e.preventDefault();
        e.stopPropagation();
    },

    getOffset: function (focusElementId) {
        var oldOffsetTop = 0;

        if (focusElementId.length > 0) {
            var focusElement = $(CPQ.core.encodeId(focusElementId));
            if ($(focusElement).offset()) {
                oldOffsetTop = $(focusElement).offset().top
                    - $(document).scrollTop();
            }
        }
        return oldOffsetTop;
    },

    doScrolling: function (focusElementId, scrollTo, oldOffsetTop) {
        var newOffsetTop = oldOffsetTop;
        if (focusElementId.length > 0 && !scrollTo) {
            var elem = $(CPQ.core.encodeId(focusElementId));
            if (elem.length > 0) {
                newOffsetTop = elem.offset().top - $(document).scrollTop();
            }
        }
        CPQ.focushandler.focusRestore(focusElementId, scrollTo, newOffsetTop
            - oldOffsetTop);
    },

    getSerializedConfigForm: function (cpqAction, focusId, forceExpand,
                                       groupIdToToggle, groupIdToToggleInSpecTree) {
        $("#cpqAction").val(cpqAction);
        $("#focusId").val(focusId);
        $("#forceExpand").val(forceExpand);
        $("#groupIdToToggle").val(groupIdToToggle);
        $("#groupIdToToggleInSpecTree").val(groupIdToToggleInSpecTree);
        var data = $("#configform").serialize();
        $("#cpqAction").val("");
        $("#focusId").val("");
        $("#forceExpand").val(false);
        $("#groupIdToToggle").val("");
        $("#groupIdToToggleInSpecTree").val("");

        return data;
    },

    updateContent: function (response) {
        var analyticState, pricingState, varaiantSearchState;

        if ($("#analyticsEnabled").text() === "true") {
            analyticState = CPQ.analyticshandler.storeAnalyticsState();
        }
        if ($("#asyncPricingMode").text() === "true") {
            pricingState = CPQ.pricinghandler.storePricingState();
        }
        varaiantSearchState = CPQ.variantsearchhandler
            .storeVariantSearchState();

        CPQ.uihandler.updateSlotContent(response, "configContentSlot");

        if (analyticState) {
            CPQ.analyticshandler.restoreAnalyticsState(analyticsState);
        }
        if (pricingState) {
            CPQ.pricinghandler.restoreValuePricingState(pricingState);
        }
        CPQ.uihandler.updateSlotContent(response, "configSidebarSlot");
        CPQ.uihandler.updateSlotContent(response, "configBottombarSlot");

        if (pricingState) {
            CPQ.pricinghandler.restorePriceSummaryState(pricingState);
        }
        CPQ.variantsearchhandler.restoreVariantSearchState(varaiantSearchState);
        CPQ.uihandler.updateSlotContent(response, "cpq-message-area");
        CPQ.config.checkValueHasChanged();
        CPQ.config.generateAriaDescribedBy();
    },

    registerAjax: function () {
        $("#configform").submit(function (e) {
            e.preventDefault();
        });
        // FF and Chrome does fire onChange when enter is pressed in input field
        // and additionally the onKeyPress event
        $("#configform :input").on("change", function (e) {
            CPQ.config.fireValueChangedPost(e);
        });
        // IE does not fire onChange when enter is pressed in input field, only
        // on focus loss
        $("#configform :input").keypress(function (e) {
            if (e.which === 13) {
                CPQ.config.fireValueChangedPost(e);
            }
        });
        $(document).ajaxError(function (event, xhr) {
            document.write(xhr.responseText);
        });

    },

    ifExpModeActive: function () {
        var result = new RegExp("expmode=true", "i")
            .exec(window.location.search);
        if (result === null) {
            return;
        }
        var selector = "label.cpq-csticlabel, label.cpq-csticValueLabel, option.cpq-csticValue, img.cpq-cstic-value-image";
        $(selector).each(
            function () {
                var idSubstring = null;
                var elementId = $(this).attr("id");
                try {
                    idSubstring = elementId.substring(0, elementId
                        .lastIndexOf('.'));
                } catch (e) {
                    return;
                }
                var tooltip = CPQ.config.getTooltipText(idSubstring);
                $(this).prop("title", tooltip);
            });
    },

    getTooltipText: function (idSubstring) {
        return idSubstring.substring(idSubstring.length, idSubstring
                .lastIndexOf('.') + 1);
    },

    checkValueHasChanged: function () {
        if (CPQ.config.lastTarget) {
            var element = document.getElementById(CPQ.config.lastTarget.id);
            if (element === null
                || element.value !== CPQ.config.lastTarget.value
                || element.checked !== CPQ.config.lastTarget.checked) {
                CPQ.config.lastTarget = null;
            }
        }
    },

    hasTargetChanged: function (e) {
        if (!CPQ.config.lastTarget || e.target.id !== CPQ.config.lastTarget.id
            || e.target.value !== CPQ.config.lastTarget.value
            || e.target.checked !== CPQ.config.lastTarget.checked) {
            CPQ.config.lastTarget = e.target;
            return true;
        }
        return false;
    },

    fireValueChangedPost: function (e) {
        if (CPQ.config.hasTargetChanged(e)) {
            var path = $(e.currentTarget).parents(".cpq-cstic").children(
                "input:hidden").attr("name");
            var data = CPQ.config.getSerializedConfigForm('VALUE_CHANGED',
                path, false, "");
            setTimeout(function () {
                CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
            }, 50);
        } else {
            e.preventDefault();
            e.stopImmediatePropagation();
        }
    },

    clickAddToCartButton: function () {
        // Postpone submit, to take care that a potential
        // update is triggered in parallel, by a focus lost
        setTimeout(function addToCartClicked() {
            var form = $("#configform")[0];
            form.setAttribute("action", CPQ.core.getAddToCartUrl());
            CPQ.core.firePost(function () {
                form.submit();
            });
        }, 100);
    },

    getDescriptionContext: function (messageFieldId, formElem) {
        var prevContent = formElem.attr('aria-describedby');
        var newContent = messageFieldId;
        if (prevContent && prevContent.indexOf(newContent) === -1) {
            newContent = prevContent + " " + newContent;
        }

        return newContent;
    },

    isElementCsticLabel: function (elem) {
        return elem.length !== 0 && elem[0].tagName.toLowerCase() === "label" && $(elem[0]).attr('class').indexOf("cpq-csticlabel" !== -1) &&
            $(elem).closest('div').attr('class').indexOf("cpq-label-config-link-row") !== -1;
    },

    generateAriaDescribedByForLabel: function (messageFieldId, csticId) {
        var selector = CPQ.core.encodeId(csticId + ".label");
        var formElem = $(selector);

        var newContent = CPQ.config.getDescriptionContext(messageFieldId, formElem);

        if (CPQ.config.isElementCsticLabel(formElem)) {
            formElem.attr('aria-describedby', newContent);
            formElem.attr('tabindex', 0);
        }
    },

    generateAriaDescribedByForInput: function (messageFieldId, csticId) {
        var ddlbButton = csticId.substr(0, csticId.lastIndexOf("."));
        var selector = CPQ.core.encodeId(csticId + ".inputNum")
            + ", " + CPQ.core.encodeId(csticId + ".input")
            + ", " + CPQ.core.encodeId(csticId + ".checkBoxList")
            + ", " + CPQ.core.encodeId(csticId + ".checkBox")
            + ", " + CPQ.core.encodeId(ddlbButton + ".ddlb-button");

        var formElem = $(selector);

        var newContent = CPQ.config.getDescriptionContext(messageFieldId, formElem);

        if (!formElem[0]) {
            csticId = csticId.substr(0, csticId.lastIndexOf("."));
            var radioGroup = $(CPQ.core.encodeId(csticId
                + ".radioGroup"));
            formElem = radioGroup.find(":checked");
            if (!formElem[0]) {
                formElem = radioGroup
                    .find(".cpq-csticValueSelect-first");
            }
        }

        formElem.attr('aria-describedby', newContent);
    },

    generateAriaDescribedBy: function () {
        // for screen reader support link any form related message with the
        // corresponding form tag
        $(".cpq-error, .cpq-conflict, .cpq-warning, .cpq-messages").each(
            function () {
                var messageFieldId = $(this).attr('id');
                var csticId = CPQ.idhandler
                    .getCsticIdFromFieldGeneric(messageFieldId);

                CPQ.config.generateAriaDescribedByForLabel(messageFieldId, csticId);
                CPQ.config.generateAriaDescribedByForInput(messageFieldId, csticId);
                CPQ.config.setTabIndexForMessageLink(messageFieldId);
            });
    },

    defineElementForRadioButton : function(elem, csticId, valueId) {
        if (!elem[0]) {
            var radioGroup = $(CPQ.core.encodeId(csticId
                + ".radioGroup"));

            elem = radioGroup.find(":checked");
            if (!elem[0]) {
                elem = radioGroup
                    .find(".cpq-csticValueSelect-first");
            }

            if (elem[0] && valueId !== undefined) {
                var selectedValueId = elem[0].id.substr(0, elem[0].id.lastIndexOf("."));
                if(selectedValueId !== valueId) {
                    return undefined;
                }
            }
        }

        return elem;
    },

    setTabIndexForMessageLink: function (messageFieldId) {
        var id = messageFieldId.substr(0, messageFieldId.lastIndexOf("."));
        $("[id^='" + id + "'][class='cpq-promo-message-link']").each(
            function () {
                var selector = CPQ.core.encodeId(id + ".label");
                var formElem = $(selector);

                if (CPQ.config.isElementCsticLabel(formElem)) {
                    $(this).attr('tabindex', 0);
                } else {
                    var csticId = id.substr(0, id.lastIndexOf("."));
                    var selector = CPQ.core.encodeId(id + ".checkBox")
                        + ", " + CPQ.core.encodeId(csticId + ".ddlb-button");

                    var formElem = $(selector);

                    formElem = CPQ.config.defineElementForRadioButton(formElem, csticId, id);

                    if (formElem !== undefined && formElem.length !== 0) {
                        if (formElem[0].checked) {
                            $(this).attr('tabindex', 0);
                        } else if($(formElem[0]).attr('role') === 'combobox') {
                            $(this).attr('tabindex', 0);
                        } else {
                            $(this).removeAttr('tabindex');
                        }
                    } else {
                        $(this).removeAttr('tabindex');
                    }
                }
            });
    }

};

$(document).ready(function () {
    if ($("#dynamicConfigContent").length > 0) {
        CPQ.core.pageType = "config";
        CPQ.core.formNameId = "#configform";
        CPQ.config.bindAll();
    }
});
