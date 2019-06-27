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

    var ID_PREFIX = 'system-alert-';

    var locators = {
        allAlerts: function() {
            return by.xpath("//*[contains(@id, '" + ID_PREFIX + "')]");
        },

        alertByIndex: function(index) {
            return by.css("#" + ID_PREFIX + index);
        },

        alertMessageByIndex: function(index) {
            return by.css("#" + ID_PREFIX + index + " > div");
        },

        closeButtonByIndex: function(index) {
            return by.css("#" + ID_PREFIX + index + " > .close");
        }

    };

    var systemAlerts = {};


    // ================ ACTIONS =================

    systemAlerts.actions = {
        closeAlertByIndex: function(index) {
            return browser.click(locators.closeButtonByIndex(index));
        },

        flush: function() {
            element.all(locators.allAlerts()).count().then(function(count) {
                for (var i = 0; i < count; i++) {
                    systemAlerts.actions.closeAlertByIndex(0);
                }
                systemAlerts.assertions.assertNoAlertsDisplayed();
            });
        }

    };


    // =============== ASSERTIONS ===============

    systemAlerts.assertions = {

        assertAlertIsOfTypeByIndex: function(index, type) {
            browser.waitForPresence(locators.alertByIndex(index), "failed to find alert by index: " + index);
            expect(locators.alertByIndex(index)).toContainClass("alert-" + type);
        },

        assertAlertCloseabilityByIndex: function(index, closeable) {
            if (closeable) {
                browser.waitToBeDisplayed(locators.closeButtonByIndex(index));
            } else {
                browser.waitForAbsence(locators.closeButtonByIndex(index));
            }
        },

        assertAlertTextByIndex: function(index, expectedText) {
            browser.waitForPresence(locators.alertMessageByIndex(index), "failed to find alert message by index: " + index);
            expect(locators.alertMessageByIndex(index)).toEqualText(expectedText);
        },

        assertNoAlertsDisplayed: function() {
            browser.waitForAbsence(locators.alertByIndex(0));
        },

        assertTotalNumberOfAlerts: function(numExpectedAlerts) {
            expect(element.all(locators.allAlerts()).count()).toBe(numExpectedAlerts);
        }

    };

    return systemAlerts;

}());
