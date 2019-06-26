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
describe('yNotificationPanel', function() {
    var pageObject = require('./yNotificationPanelTestPageObject.js');
    var yNotificationPanelObject = require('../utils/components/yNotificationPanelComponentObject.js');

    describe('Notifications', function() {
        beforeEach(function(done) {
            pageObject.actions.navigateToTestPage().then(function() {
                pageObject.actions.resetForm();
                pageObject.actions.removeAllNotifications();

                done();
            });
        });

        describe('List', function() {
            describe('Push', function() {
                it('A notification is present once it is added', function() {
                    // Given
                    pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                    // Then
                    yNotificationPanelObject.assertions.assertNumberOfNotifications(1);
                    yNotificationPanelObject.assertions.assertNotificationPresenceById(pageObject.constants.NOTIFICATION_ID);
                });

                it('A notification is present at the top of the list once it is added', function() {
                    // Given
                    pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID1, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                    // When
                    pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID2, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                    // Then
                    yNotificationPanelObject.assertions.assertNumberOfNotifications(2);
                    yNotificationPanelObject.assertions.assertNotificationPresenceById(pageObject.constants.NOTIFICATION_ID2);
                    yNotificationPanelObject.assertions.assertNotificationIdByIndex(0, pageObject.constants.NOTIFICATION_ID2);
                });
            });

            describe('Remove', function() {
                it('A notification is no longer present once it is removed', function() {
                    // Given
                    pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                    // When
                    pageObject.actions.removeNotification(pageObject.constants.NOTIFICATION_ID);

                    // Then
                    yNotificationPanelObject.assertions.assertNumberOfNotifications(0);
                    yNotificationPanelObject.assertions.assertNotificationAbsenceById(pageObject.constants.NOTIFICATION_ID);
                });
            });

            describe('Remove All', function() {
                it('All notifications are no longer present once they are all removed', function() {
                    // Given
                    pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID1, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);
                    pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID2, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);
                    pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID3, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                    // When
                    pageObject.actions.removeAllNotifications();

                    // Then
                    yNotificationPanelObject.assertions.assertNumberOfNotifications(0);
                    yNotificationPanelObject.assertions.assertNotificationAbsenceById(pageObject.constants.NOTIFICATION_ID1);
                    yNotificationPanelObject.assertions.assertNotificationAbsenceById(pageObject.constants.NOTIFICATION_ID2);
                    yNotificationPanelObject.assertions.assertNotificationAbsenceById(pageObject.constants.NOTIFICATION_ID3);
                });
            });
        });

        describe('List Item', function() {
            it('A list item renders the notification HTML template', function() {
                // Given
                pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                // Then
                yNotificationPanelObject.assertions.assertNotificationTemplateById(
                    pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);
            });

            it('A list item renders the notification template from a template URL', function() {
                // Given
                pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID, null, pageObject.constants.NOTIFICATION_TEMPLATE_URL);

                // Then
                yNotificationPanelObject.assertions.assertNotificationTemplateById(
                    pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);
            });
        });

        describe('Panel', function() {
            it('The panel disappears when it contains at least one notification and the mouse hovers over it', function() {
                // Given
                pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                // When
                yNotificationPanelObject.actions.moveMousePointerOverNotificationPanel();

                // Then
                yNotificationPanelObject.assertions.assertNotificationPanelNotDisplayed();
            });

            it('The panel re-appears when it contains at least one notification and the mouse leaves its bounds after it had disappeared due to a mouse over', function() {
                // Given
                pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                yNotificationPanelObject.actions.moveMousePointerOverNotificationPanel();

                // When
                yNotificationPanelObject.actions.moveMousePointerOutOfNotificationPanel();

                // Then
                yNotificationPanelObject.assertions.assertNotificationPanelDisplayed();
            });

            it('An element behind the notification panel is not clickable when a notification is displayed over it', function() {
                // Given
                pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                // Then
                pageObject.assertions.assertClickThroughCheckboxNotClickable();
            });

            it('An element behind the notification panel is clickable when the mouse pointer is moved over the panel to hide it', function() {
                // Given
                pageObject.actions.pushNotification(pageObject.constants.NOTIFICATION_ID, pageObject.constants.NOTIFICATION_TEMPLATE_CONTENT);

                yNotificationPanelObject.actions.moveMousePointerOverNotificationPanel();

                // When
                pageObject.actions.clickClickThroughCheckbox();

                // Then
                pageObject.assertions.assertClickThroughCheckboxSelected();
            });
        });
    });

    describe('Storefront', function() {
        beforeEach(function(done) {
            pageObject.actions.navigateToStorefront().then(function() {
                done();
            });
        });

        describe('Panel', function() {
            it('The panel disappears when it contains at least one notification and the mouse hovers over it', function() {
                // When
                yNotificationPanelObject.actions.moveMousePointerOverNotificationPanel();

                // Then
                yNotificationPanelObject.assertions.assertNotificationPanelNotDisplayed();
            });

            it('The panel re-appears when it contains at least one notification and the mouse leaves its bounds after it had disappeared due to a mouse over', function() {
                // Given
                yNotificationPanelObject.actions.moveMousePointerOverNotificationPanel();

                // When
                yNotificationPanelObject.actions.moveMousePointerOutOfNotificationPanel();

                // Then
                yNotificationPanelObject.assertions.assertNotificationPanelDisplayed();
            });

            it('An element behind the notification panel is not clickable when a notification is displayed over it', function() {
                pageObject.assertions.assertClickThroughCheckboxInIFrameNotClickable();
            });

            it('An element behind the notification panel is clickable when the mouse pointer is moved over the panel to hide it', function() {
                // Given
                yNotificationPanelObject.actions.moveMousePointerOverNotificationPanel();


                // When
                pageObject.actions.clickClickThroughCheckboxInIFrame();

                // Then
                pageObject.assertions.assertClickThroughCheckboxSelected();
            });
        });
    });
});
