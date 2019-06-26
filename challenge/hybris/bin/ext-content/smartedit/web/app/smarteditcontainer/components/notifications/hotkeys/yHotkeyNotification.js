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
angular.module('yHotkeyNotificationModule', [])
    /*
     * This component renders the hotke notification template which includes the required key(s),
     * a title and optional message.
     */
    .component('yHotkeyNotification', {
        templateUrl: 'yHotkeyNotificationTemplate.html',
        controller: function() {},
        bindings: {
            hotkeyNames: '<',
            title: '<',
            message: '<?'
        }
    });
