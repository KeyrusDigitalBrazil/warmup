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

    var selectComponentObjectFactory = require('../utils/components/standardHtml/selectComponentObject');

    var locators = {
        showButton: function() {
            return by.css('#test-alert-add-button');
        },
        resetButton: function() {
            return by.css('#test-alert-reset-button');
        },
        alertMessage: function() {
            return by.css('#test-alert-message');
        },
        alertMessagePlaceholders: function() {
            return by.css('#test-alert-message-placeholder');
        },
        alertType: function() {
            return by.css('#test-alert-type');
        },
        alertCloseable: function() {
            return by.css('#test-alert-closeable');
        },
        alertTimeout: function() {
            return by.css('#test-alert-timeout');
        },
        alertTemplate: function() {
            return by.css('#test-alert-template');
        },
        alertTemplateUrl: function() {
            return by.css('#test-alert-templateUrl');
        }
    };

    var systemAlertsPageObject = {};


    systemAlertsPageObject.actions = {

        navigate: function() {
            return browser.get('test/e2e/systemAlerts/index.html');

            /**
             * Don't wait below: container looks for toolbars, but this setup doesn't load them
             */
            // browser.waitForContainerToBeReady();
        },

        setMessage: function(message) {
            return browser.sendKeys(locators.alertMessage(), message);
        },

        setMessagePlaceholders: function(messagePlaceholders) {
            return browser.sendKeys(locators.alertMessagePlaceholders(), messagePlaceholders);
        },

        setType: function(type) {
            var alertTypeDropDown = selectComponentObjectFactory.byLocator(locators.alertType());
            return alertTypeDropDown.actions.selectOptionByText(type);
        },

        setTimeout: function(millis) {
            return browser.sendKeys(locators.alertTimeout(), millis);
        },

        setCloseable: function(closeable) {
            element(locators.alertCloseable()).isSelected().then(function(isSelect) {
                if (closeable !== isSelect) {
                    browser.click(locators.alertCloseable());
                }
            });
        },

        setTemplate: function(template) {
            return browser.sendKeys(locators.alertTemplate(), template);
        },

        setTemplateUrl: function(templateUrl) {
            return browser.sendKeys(locators.alertTemplateUrl(), templateUrl);
        },

        showAlert: function() {
            return browser.click(locators.showButton());
        },

        resetForm: function() {
            return browser.click(locators.resetButton());
        }
    };

    systemAlertsPageObject.showAlert = function(message, type, closeable, timeout, template, templateUrl, messagePlaceholders) {
        this.actions.resetForm();
        if (message) {
            this.actions.setMessage(message);
        }
        if (messagePlaceholders) {
            this.actions.setMessagePlaceholders(messagePlaceholders);
        }
        if (template) {
            this.actions.setTemplate(template);
        }
        if (templateUrl) {
            this.actions.setTemplateUrl(templateUrl);
        }
        if (type) {
            this.actions.setType(type);
        }
        if (typeof closeable === "boolean") {
            this.actions.setCloseable(closeable);
        }
        if (timeout) {
            this.actions.setTimeout(timeout);
        }
        this.actions.showAlert();
    };

    return systemAlertsPageObject;

}());
