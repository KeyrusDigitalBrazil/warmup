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
module.exports = function() {

    function ClickThroughOverlayComponentObject() {

        // ================== ELEMENTS ==================

        var elements = {};
        elements.allowClickThroughButton = function() {
            return browser.findElement(by.id('smartEditPerspectiveToolbar_option_se.CLICK_THROUGH_OVERLAY_btn'));
        };
        elements.preventClickThroughButton = function() {
            return browser.findElement(by.id('smartEditPerspectiveToolbar_option_se.PREVENT_OVERLAY_CLICKTHROUGH_btn'));
        };



        var page = {};

        // ================== ACTIONS ==================

        page.actions = {};
        page.actions.allowClickThroughOverlay = function() {
            return browser.switchToParent().then(function() {
                return browser.click(elements.allowClickThroughButton());
            });
        };
        page.actions.preventClickThroughOverlay = function() {
            return browser.switchToParent().then(function() {
                return browser.click(elements.preventClickThroughButton());
            });
        };

        // =================== UTILS ==================

        page.utils = {};
        page.utils.clickThroughOverlay = function(element) {
            return page.actions.allowClickThroughOverlay().then(function() {
                return browser.switchToIFrame().then(function() {
                    return browser.click(element).then(function() {
                        return page.actions.preventClickThroughOverlay();
                    });
                });
            });
        };

        return page;
    }

    return new ClickThroughOverlayComponentObject();
}();
