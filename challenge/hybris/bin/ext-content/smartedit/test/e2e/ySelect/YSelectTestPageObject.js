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
module.exports = (function() {
    var ySelectTestPageObject = function(id) {

        this.id = id;

        // --------------------------------------------------------------------------------------------------
        // Elements
        // --------------------------------------------------------------------------------------------------
        this.elements = {
            getSelectorResult: function() {
                return element(by.id("model-" + this.id)).getText(function(text) {
                    return text.trim();
                });
            }.bind(this),
            getErrorButton: function() {
                return element(by.id("error-" + this.id));
            }.bind(this),
            getWarningButton: function() {
                return element(by.id("warning-" + this.id));
            }.bind(this),
            getResetValidationButton: function() {
                return element(by.id("reset-" + this.id));
            }.bind(this),
            getChangeSourceButton: function() {
                return element(by.id("source-" + this.id));
            }.bind(this),
            getForceResetCheckbox: function() {
                return element(by.id("force-reset-" + this.id));
            }.bind(this)
        };

        // --------------------------------------------------------------------------------------------------
        // Actions
        // --------------------------------------------------------------------------------------------------
        this.actions = {
            clickShowErrorButton: function() {
                return browser.click(this.elements.getErrorButton());
            }.bind(this),
            clickShowWarningButton: function() {
                return browser.click(this.elements.getWarningButton());
            }.bind(this),
            clickResetValidationButton: function() {
                return browser.click(this.elements.getResetValidationButton());
            }.bind(this),
            clickChangeSourceButton: function() {
                return browser.click(this.elements.getChangeSourceButton());
            }.bind(this),
            clickForceResetCheckBox: function() {
                return browser.click(this.elements.getForceResetCheckbox());
            }.bind(this)
        };


        // --------------------------------------------------------------------------------------------------
        // Assertions
        // --------------------------------------------------------------------------------------------------
        this.assertions = {
            assertSelectorModelIsEqualTo: function(expectedText) {
                this.elements.getSelectorResult().then(function(text) {
                    expect(text).toBe(expectedText);
                });
            }.bind(this),
        };
    };

    return ySelectTestPageObject;
})();
