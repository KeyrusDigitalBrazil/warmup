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
    var YNOTIFICATION_ID_PREFIX = 'y-notification-';

    var yNotificationPanelComponentObject = {};

    yNotificationPanelComponentObject.elements = {
        notificationPanel: function() {
            return browser.switchToParent().then(function() {
                return element(by.css('div.y-notification-panel'));
            });
        },
        allNotifications: function() {
            return browser.switchToParent().then(function() {
                return element.all(by.xpath('//*[contains(@id, "' + YNOTIFICATION_ID_PREFIX + '")]'));
            });
        },
        notificationById: function(notificationId) {
            return browser.switchToParent().then(function() {
                return element(by.css('div.y-notification-panel y-notification div[id*="' + notificationId + '"]'));
            });
        },
        notificationByIndex: function(index) {
            return yNotificationPanelComponentObject.elements.allNotifications().then(function(notifications) {
                return notifications[index];
            });
        }
    };

    yNotificationPanelComponentObject.actions = {
        moveMousePointerOverNotificationPanel: function() {
            return browser.switchToParent().then(function() {
                return yNotificationPanelComponentObject.elements.notificationPanel().then(function(notificationPanel) {
                    return browser.actions()
                        .mouseMove(notificationPanel)
                        .perform();
                });
            });
        },
        moveMousePointerOutOfNotificationPanel: function() {
            var offset = {
                x: -10,
                y: -10
            };

            return browser.switchToParent().then(function() {
                return yNotificationPanelComponentObject.elements.notificationPanel().then(function(notificationPanel) {
                    return browser.actions()
                        .mouseMove(notificationPanel, offset)
                        .perform();
                });
            });
        }
    };

    yNotificationPanelComponentObject.assertions = {
        assertNotificationPanelDisplayed: function() {
            yNotificationPanelComponentObject.elements.notificationPanel().then(function(notificationPanel) {
                browser.waitToBeDisplayed(notificationPanel,
                    'could not assert that the notification panel is displayed');
            });
        },
        assertNotificationPanelNotDisplayed: function() {
            yNotificationPanelComponentObject.elements.notificationPanel().then(function(notificationPanel) {
                browser.waitNotToBeDisplayed(notificationPanel,
                    'could not assert that the notification panel is not displayed');
            });
        },
        assertNotificationPresenceById: function(notificationId) {
            yNotificationPanelComponentObject.elements.notificationById(notificationId).then(function(notification) {
                browser.waitForPresence(notification,
                    'could not assert presence of notification with ID "' + notificationId + '"');
            });
        },
        assertNotificationAbsenceById: function(notificationId) {
            yNotificationPanelComponentObject.elements.notificationById(notificationId).then(function(notification) {
                browser.waitForAbsence(notification,
                    'could not assert absence of notification with ID "' + notificationId + '"');
            });
        },
        assertNotificationTemplateById: function(notificationId, template) {
            yNotificationPanelComponentObject.elements.notificationById(notificationId).then(function(notification) {
                expect(browser.getInnerHTML(notification)).toContain(template);
            });
        },
        assertNotificationIdByIndex: function(index, notificationId) {
            yNotificationPanelComponentObject.elements.notificationByIndex(index).then(function(notification) {
                expect(notification.getAttribute('id')).toEqual(YNOTIFICATION_ID_PREFIX + notificationId);
            });
        },
        assertNumberOfNotifications: function(numberOfNotifications) {
            yNotificationPanelComponentObject.elements.allNotifications().then(function(notifications) {
                expect(notifications.length).toBe(numberOfNotifications);
            });
        }
    };

    return yNotificationPanelComponentObject;
})();
