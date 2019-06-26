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
    var yNotificationPanelTestPageObject = {};

    function assertClickThroughCheckboxSelected(isSelected) {
        expect(yNotificationPanelTestPageObject.elements.clickThroughCheckbox().isSelected())
            .toBe(isSelected);
    }

    function assertClickThroughCheckboxNotClickable(clickPromise) {
        var onSuccess = function() {
            return true;
        };

        var onError = function() {
            return false;
        };

        expect(clickPromise.then(onSuccess, onError)).toBe(false);
    }

    yNotificationPanelTestPageObject.constants = {
        NOTIFICATION_ID: 'test.notification.id',
        NOTIFICATION_ID1: this.NOTIFICATION_ID + '1',
        NOTIFICATION_ID2: this.NOTIFICATION_ID + '2',
        NOTIFICATION_ID3: this.NOTIFICATION_ID + '3',

        NOTIFICATION_TEMPLATE_URL: 'testNotificationTemplate.html',
        NOTIFICATION_TEMPLATE_CONTENT: 'This is a test notification template.'
    };

    yNotificationPanelTestPageObject.elements = {
        notificationId: function() {
            return element(by.id('test-notification-id'));
        },
        notificationTemplate: function() {
            return element(by.id('test-notification-template'));
        },
        notificationTemplateUrl: function() {
            return element(by.id('test-notification-template-url'));
        },
        pushButton: function() {
            return element(by.id('test-notification-push-button'));
        },
        removeButton: function() {
            return element(by.id('test-notification-remove-button'));
        },
        removeAllButton: function() {
            return element(by.id('test-notification-remove-all-button'));
        },
        resetButton: function() {
            return element(by.id('test-notification-reset-button'));
        },
        clickThroughCheckbox: function() {
            return element(by.id('test-notification-clickthrough-checkbox'));
        },
        goToStorefrontButton: function() {
            return element(by.id('test-notification-goto-storefront'));
        }
    };

    yNotificationPanelTestPageObject.actions = {
        navigateToTestPage: function() {
            return browser.get('test/e2e/notifications/index.html');
        },
        navigateToStorefront: function() {
            yNotificationPanelTestPageObject.actions.navigateToTestPage();
            yNotificationPanelTestPageObject.actions.clickGoToStorefrontButton();

            return browser.waitForWholeAppToBeReady();
        },

        setNotificationId: function(notificationId) {
            return browser.sendKeys(yNotificationPanelTestPageObject.elements.notificationId(),
                notificationId);
        },
        setNotificationTemplate: function(template) {
            return browser.sendKeys(yNotificationPanelTestPageObject.elements.notificationTemplate(),
                template);
        },
        setNotificationTemplateUrl: function(templateUrl) {
            return browser.sendKeys(yNotificationPanelTestPageObject.elements.notificationTemplateUrl(),
                templateUrl);
        },

        clickPushButton: function() {
            return browser.click(yNotificationPanelTestPageObject.elements.pushButton());
        },
        clickRemoveButton: function() {
            return browser.click(yNotificationPanelTestPageObject.elements.removeButton());
        },
        clickRemoveAllButton: function() {
            return browser.click(yNotificationPanelTestPageObject.elements.removeAllButton());
        },
        clickResetButton: function() {
            return browser.click(yNotificationPanelTestPageObject.elements.resetButton());
        },
        clickClickThroughCheckbox: function() {
            return browser.click(yNotificationPanelTestPageObject.elements.clickThroughCheckbox());
        },
        clickClickThroughCheckboxInIFrame: function() {
            return browser.switchToIFrame().then(function() {
                return yNotificationPanelTestPageObject.actions.clickClickThroughCheckbox();
            });
        },
        clickGoToStorefrontButton: function() {
            return browser.click(yNotificationPanelTestPageObject.elements.goToStorefrontButton());
        },

        resetForm: function() {
            return yNotificationPanelTestPageObject.actions.clickResetButton();
        },

        pushNotification: function(notificationId, template, templateUrl) {
            yNotificationPanelTestPageObject.actions.resetForm();

            if (notificationId) {
                yNotificationPanelTestPageObject.actions.setNotificationId(notificationId);
            }

            if (template) {
                yNotificationPanelTestPageObject.actions.setNotificationTemplate(template);
            }

            if (templateUrl) {
                yNotificationPanelTestPageObject.actions.setNotificationTemplateUrl(templateUrl);
            }

            return yNotificationPanelTestPageObject.actions.clickPushButton();
        },
        removeNotification: function(notificationId) {
            yNotificationPanelTestPageObject.actions.resetForm();

            if (notificationId) {
                yNotificationPanelTestPageObject.actions.setNotificationId(notificationId);
            }

            return yNotificationPanelTestPageObject.actions.clickRemoveButton();
        },
        removeAllNotifications: function() {
            return yNotificationPanelTestPageObject.actions.clickRemoveAllButton();
        }
    };

    yNotificationPanelTestPageObject.assertions = {
        assertClickThroughCheckboxSelected: function() {
            assertClickThroughCheckboxSelected(true);
        },
        assertClickThroughCheckboxNotSelected: function() {
            assertClickThroughCheckboxSelected(false);
        },
        assertClickThroughCheckboxNotClickable: function() {
            assertClickThroughCheckboxNotClickable(
                yNotificationPanelTestPageObject.actions.clickClickThroughCheckbox());
        },
        assertClickThroughCheckboxInIFrameNotClickable: function() {
            assertClickThroughCheckboxNotClickable(
                yNotificationPanelTestPageObject.actions.clickClickThroughCheckboxInIFrame());
        }
    };

    return yNotificationPanelTestPageObject;
})();
