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
angular.module('notificationMocksModule', [
        'smarteditServicesModule'
    ])
    .run(function(notificationService) {
        var MOCK_NOTIFICATION_ID = 'MOCK_NOTIFICATION';
        var MOCK_NOTIFICATION_TEMPLATE = 'This is a mock notification.';
        var MOCK_NOTIFICATION_COUNT = 5;

        var mockNotificationIndex = 0;

        while (mockNotificationIndex++ < MOCK_NOTIFICATION_COUNT) {
            notificationService.pushNotification({
                id: MOCK_NOTIFICATION_ID + mockNotificationIndex,
                template: MOCK_NOTIFICATION_TEMPLATE
            });
        }
    });

try {
    angular.module('smarteditloader').requires.push('notificationMocksModule');
    angular.module('smarteditcontainer').requires.push('notificationMocksModule');
} catch (exception) {
    console.error('yNotificationMocks - Failed to add notificationMocksModule as a dependency', exception);
}
