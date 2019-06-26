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
angular.module('customViewModule', [
        'yNotificationPanelModule',
        'resourceLocationsModule'
    ])
    .constant('PATH_TO_CUSTOM_VIEW', '../../test/e2e/notifications/customView.html')
    .controller('customViewController', function(notificationService, experienceService) {

        this.pushNotification = function() {
            if (this.configuration.template.length < 1) {
                delete this.configuration.template;
            }

            if (this.configuration.templateUrl.length < 1) {
                delete this.configuration.templateUrl;
            }

            notificationService.pushNotification(this.configuration);

            this.reset();
        };

        this.removeNotification = function() {
            notificationService.removeNotification(this.configuration.id);
        };

        this.removeAllNotifications = function() {
            notificationService.removeAllNotifications();
        };

        this.reset = function() {
            this.configuration = {
                id: '',
                template: '',
                templateUrl: ''
            };
        };

        this.goToStorefront = function() {
            experienceService.loadExperience({
                siteId: "apparel-uk",
                catalogId: "apparel-ukContentCatalog",
                catalogVersion: "Staged"
            });
        };

        this.reset();
    });
