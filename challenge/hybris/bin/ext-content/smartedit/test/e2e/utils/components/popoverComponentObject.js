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
module.exports = {

    _getAnchorYHelpSelector: function(selector) {
        return by.css(selector + " [data-y-popover]");
    },
    _getAnchorYPopoverSector: function(selector) {
        return by.css(selector + "[data-y-popover]");
    },
    _getAnchorYHelp: function(selector) {
        return element(this._getAnchorYHelpSelector(selector));
    },
    _getAnchorYPopover: function(selector) {
        return element(this._getAnchorYPopoverSector(selector));
    },
    getPopover: function() {
        return element(by.css('.popover'));
    },
    hoverYHelp: function(selector) {
        return browser.actions().mouseMove(this._getAnchorYHelp(selector)).perform();
    },
    hoverYPopover: function(selector) {
        return browser.actions().mouseMove(this._getAnchorYPopover(selector)).perform();
    },
    clickYHelp: function(selector) {
        return browser.click(this._getAnchorYHelpSelector(selector));
    },
    clickYPopover: function(selector) {
        return browser.click(this._getAnchorYPopoverSector(selector));
    },
    getTitleBox: function() {
        return element(by.css('.popover .popover-title'));
    },
    getTitleText: function() {
        return this.getTitleBox('.popover .popover-title').getText();
    },
    getBodyText: function() {
        return element(by.css('.popover .popover-content')).getText().then(function(text) {
            return text.replace(/\n|\r/g, " ");
        });
    }
};
