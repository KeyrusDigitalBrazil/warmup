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
CPQ.menuhandler = {

    menuOpen: function (e) {
        var sideBar = $("#configSidebarSlot").parent();
        sideBar.removeClass("hidden-xs hidden-sm");
        var menu = $("#cpqMenuArea");
        menu.addClass("hidden-xs hidden-sm");
        var config = $("#configContentSlot").parent();
        config.addClass("hidden-xs hidden-sm");
        config.removeClass("col-xs-12 col-sm-12");
        // show footer header
        $(".main-footer").removeClass("hidden-xs hidden-sm");
        $(".main-header").removeClass("hidden-xs hidden-sm");
        $(".product-details-header").removeClass("hidden-xs hidden-sm");
        // hide footer header
        $(".main-footer").addClass("hidden-xs hidden-sm");
        $(".main-header").addClass("hidden-xs hidden-sm");
        $("#product-details-header").addClass("hidden-xs hidden-sm");
        CPQ.focushandler.focusRestore($(".cpq-menu-leaf-selected").attr("id"),
            true, $(window).height() / 4);
        e.preventDefault();
        e.stopPropagation();
    },

    menuRemove: function () {
        var sideBar = $("#configSidebarSlot").parent();
        sideBar.addClass("hidden-xs hidden-sm");
        var menu = $("#cpqMenuArea");
        menu.removeClass("hidden-xs hidden-sm");
        var config = $("#configContentSlot").parent();
        config.removeClass("hidden-xs hidden-sm");
        config.addClass("col-xs-12 col-sm-12");
        // show footer header
        $(".main-footer").removeClass("hidden-xs hidden-sm");
        $(".main-header").removeClass("hidden-xs hidden-sm");
        $("#product-details-header").removeClass("hidden-xs hidden-sm");
    },

    menuClose: function (e) {
        CPQ.menuhandler.menuRemove();
        CPQ.focushandler.focusOnFirstInput();
        e.preventDefault();
        e.stopPropagation();
    },

    menuGroupToggle: function (e) {
        var menuNodeId = $(e.currentTarget).attr("id");
        var nodeId = CPQ.idhandler.getGroupIdFromMenuNodeId(menuNodeId);
        $("#autoExpand").val(false);
        var data = CPQ.config.getSerializedConfigForm("MENU_NAVIGATION", "",
            false, "", nodeId);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    menuNavigation: function (e) {
        var menuNodeId = $(e.currentTarget).attr("id");
        var nodeId = CPQ.idhandler.getGroupIdFromMenuNodeId(menuNodeId);
        $("#groupIdToDisplay").val(nodeId);
        var data = CPQ.config.getSerializedConfigForm("MENU_NAVIGATION", "",
            true, nodeId);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data, "##first##"]);
        CPQ.menuhandler.menuRemove();
    }

};