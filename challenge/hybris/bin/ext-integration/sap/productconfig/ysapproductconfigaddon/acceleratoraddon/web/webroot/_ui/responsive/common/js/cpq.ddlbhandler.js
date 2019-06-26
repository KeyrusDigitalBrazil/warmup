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
CPQ.ddlbhandler = {

    cpqDdlb: function () {
        $.widget("config.ddlbselectmenu", $.ui.selectmenu, {
            _renderItem: function (ul, item) { // NOSONAR
                var wrapper = CPQ.ddlbhandler.createLineItemForCpqDdlb(item);
                var li = $("<li>");
                return li.append(wrapper).appendTo(ul);
            },

            _resizeMenu: function () { // NOSONAR
                var width = this.button.outerWidth();
                this.menu.outerWidth(width);
            },

            _renderButtonItem: function (item) { // NOSONAR
                var optionId = CPQ.idhandler.getIdFromDDLB(item.element[0].id);
                var wrapper = CPQ.ddlbhandler.createLineItemForCpqDdlb(item, true);
                wrapper.addClass("cpq-csticValue");
                var ddlbOptionId = optionId.concat(".ddlbOption");
                wrapper.attr("id", ddlbOptionId);
                CPQ.ddlbhandler.generateDDLBAriaLabelledBy(this.button, optionId);

                return wrapper;
            }
        });

        $(".cpq-ddlb").ddlbselectmenu().each(function () {
            $(this).ddlbselectmenu("menuWidget").addClass("cpq-ddlb-option");
        });

        $(".cpq-ddlb").ddlbselectmenu(
            {
                change: function (event, ui) {
                    var path = ui.item.element[0].parentElement
                        .getAttribute("name");
                    var data = CPQ.config.getSerializedConfigForm(
                        'VALUE_CHANGED', path, false, "");
                    CPQ.core.firePost(CPQ.config.doUpdatePost, [event,
                        data]);
                }
            });
    },

    createLineItemForCpqDdlb: function (item, withIcon) {
        var optionId = CPQ.idhandler.getIdFromDDLB(item.element[0].id);
        var wrapper = $("<div>");
        var styleClass = item.element.attr("data-promostyle");
        if (styleClass) {
            wrapper.addClass(styleClass);
        }
        //cpq-option-savings
        var obsoletePriceStyle = item.element.attr("data-isdeltaprice") === "true" ? "cpq-option-savings"
            : "cpq-option-strikethrough";
        $("<span>", {
            text: item.label,
            "class": "cpq-option-text"
        }).appendTo(wrapper);
        var priceWrapper = $("<span>", {
            "class": "cpq-valuePrices"
        }).appendTo(wrapper);

        $("<span>", {
            text: item.element.attr("data-price"),
            "id": optionId.concat(".valuePrice"),
            "class": "cpq-option-price"
        }).appendTo(priceWrapper);
        $("<span>", {
            text: item.element.attr("data-obsolete-price"),
            "id": optionId.concat(".strikeThroughPrice"),
            "class": obsoletePriceStyle
        }).appendTo(priceWrapper);
        if (withIcon) {
            $("<span>", {
                "class": "cpq-option-icon"
            }).appendTo(wrapper);
        }

        wrapper.addClass(styleClass);
        return wrapper;
    },

    generateDDLBAriaLabelledBy: function (button, optionId) {
        var labelId = CPQ.idhandler.getIdFromDDLB(optionId).concat(".label");
        var ddlbOptionId = optionId.concat(".ddlbOption");
        var valuePrice = optionId.concat(".valuePrice");
        var strikeThroughPrice = optionId.concat(".strikeThroughPrice");

        var labelledby = labelId.concat(" ", ddlbOptionId);
        labelledby = labelledby.concat(" ", strikeThroughPrice);
        labelledby = labelledby.concat(" ", valuePrice);

        button.attr("aria-labelledby", labelledby);
    },

    refreshCpqDdlb: function () {
        $(".cpq-ddlb").ddlbselectmenu().each(function () {
            $(this).ddlbselectmenu("refresh");
        });
    }
};
