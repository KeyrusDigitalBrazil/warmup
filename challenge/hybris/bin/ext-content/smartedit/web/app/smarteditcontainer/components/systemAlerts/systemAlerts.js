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
/**
 * @ngdoc overview
 * @name systemAlertsModule
 * @description
 * # systemAlertsModule
 * The systemAlertsModule contains the systemAlerts component.
 */
angular.module('systemAlertsModule', [
        'alertCollectionFacadesModule',
        'alertFactoryModule',
        'smarteditCommonsModule'
    ])

    /**
     * @ngdoc directive
     * @name systemAlertsModule.systemAlerts
     * @restrict E
     * @scope
     * @description
     * The systemAlerts component provides the view layer for system alerts. It renders alerts triggered both from
     * the {@link alertServiceModule.alertService alertService} or the lower layer {@link alertFactoryModule.alertFactory alertFactory}<br />
     * <br />
     * Only use one (1) instance of the systemAlerts component and use it at the root. If you remove this unique instance
     * of the component no alerts will be rendered. If you add multiple instances of the component, multiple alerts will
     * be rendered, which could cause instability in the SmartEdit web application or the test suite.
     */
    .component('systemAlerts', {
        templateUrl: 'systemAlertsTemplate.html',
        controller: ['alertCollectionComponentFacade', 'SE_ALERT_SERVICE_TYPES', function(alertCollectionComponentFacade, SE_ALERT_SERVICE_TYPES) {

            this.$onInit = function() {
                this.getAlerts = function() {
                    return alertCollectionComponentFacade.getAlerts();
                };
            };

            this.getIconType = function(type) {
                switch (type) {
                    case SE_ALERT_SERVICE_TYPES.SUCCESS:
                        return 'hyicon-msgsuccess';
                    case SE_ALERT_SERVICE_TYPES.WARNING:
                        return 'hyicon-msgwarning';
                    case SE_ALERT_SERVICE_TYPES.DANGER:
                        return 'hyicon-msgdanger';
                }
                return 'hyicon-msginfo';
            };

        }]
    });
