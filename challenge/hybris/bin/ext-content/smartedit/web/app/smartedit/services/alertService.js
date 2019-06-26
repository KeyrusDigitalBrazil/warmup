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
 * @name alertServiceModule
 * @description
 * Smartedit has only an empty (proxied) implementation of alertService.<br />
 * See the alertServiceModule under SmartEdit Container for further details.
 */
angular.module('alertServiceModule', ['alertsBoxModule', 'smarteditServicesModule'])

    .constant("SE_ALERT_SERVICE_GATEWAY_ID", 'SE_ALERT_SERVICE_GATEWAY_ID')

    /**
     * @ngdoc service
     * @name alertServiceModule.alertService
     * @description
     * Smartedit has only an empty (proxied) implementation of alertService.<br />
     * See the alertServiceModule under SmartEdit Container for further details.
     */
    .factory('alertService', function(gatewayProxy, SE_ALERT_SERVICE_GATEWAY_ID) {

        var AlertService = function() {

            gatewayProxy.initForService(this, null, SE_ALERT_SERVICE_GATEWAY_ID);

        };

        AlertService.prototype.showAlert = function() {};

        AlertService.prototype.showInfo = function() {};

        AlertService.prototype.showDanger = function() {};

        AlertService.prototype.showWarning = function() {};

        AlertService.prototype.showSuccess = function() {};


        // LEGACY!!! - should use showXY functions above

        AlertService.prototype.pushAlerts = function() {};

        AlertService.prototype.removeAlertById = function() {};

        return new AlertService();
    });
