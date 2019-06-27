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
angular.module('yNotificationModule', [])
    /**
     * The yNotification controller is responsible for determining whether the notification
     * has a template or template URL.
     */
    .controller('YNotificationController', function() {
        this.$onInit = function() {
            this.id = this.notification && this.notification.id ? 'y-notification-' + this.notification.id : '';
        };

        this.hasTemplate = function() {
            return this.notification.hasOwnProperty('template');
        };

        this.hasTemplateUrl = function() {
            return this.notification.hasOwnProperty('templateUrl');
        };
    })
    /*
     * This component renders an notification's template or template URL.
     * 
     * Here is an example of a notification object with a template URL:
     * {
     *     id: 'notification.identifier',
     *     templateUrL: 'notificationTemplateUrl.html'
     * }
     */
    .component('yNotification', {
        templateUrl: 'yNotificationTemplate.html',
        controller: 'YNotificationController',
        bindings: {
            notification: '<'
        }
    });
