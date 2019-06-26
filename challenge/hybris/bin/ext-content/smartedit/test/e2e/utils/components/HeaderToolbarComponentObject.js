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

    var componentObject = {};
    var configurations;
    if (typeof require !== 'undefined') {
        configurations = require('../components/Configurations.js');
    }

    componentObject.elements = {
        getUserAccountButton: function() {
            return element(by.id('userAccountDropdown'));
        },
        getLogoutButton: function() {
            return element(by.css('a.se-sign-out__link'));
        },
        getLanguageSelector: function(id) {
            return element(by.id(id));
        }
    };

    componentObject.actions = {
        clickOnLogout: function() {
            return browser.click(componentObject.elements.getUserAccountButton()).then(function() {
                browser.wait(protractor.ExpectedConditions.elementToBeClickable(componentObject.elements.getLogoutButton()), 5000, "Timed out waiting for logout button");
                return browser.click(componentObject.elements.getLogoutButton());
            });
        }
    };

    componentObject.assertions = {
        assertConfigurationCenterIsAbsent: function() {
            browser.waitForAbsence(configurations.getConfigurationCenterButton());
        },
        assertLanguageSelectorIsPresent: function(id) {
            browser.waitForPresence(componentObject.elements.getLanguageSelector(id));
        },
        waitForUrlToMatch: function() {
            browser.waitForUrlToMatch(/^(?!.*storefront)/);
        },
        localizedFieldIsTranslated: function(by, expectedText) {
            browser.waitUntil(function() {
                return element(by).getText().then(function(actualText) {
                    return actualText === expectedText;
                });
            });
        }
    };

    return componentObject;

}();
