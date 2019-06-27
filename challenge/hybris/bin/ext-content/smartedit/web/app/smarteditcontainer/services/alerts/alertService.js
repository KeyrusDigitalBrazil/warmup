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
 * <h1>The Alert service module</h1>
 * The alert service module provides a centralize service for displaying alerts to the user,
 * both from the container application, and the iframed application.<br />
 * <br />
 * The alerts are displayed using the {@link systemAlertsModule.systemAlerts systemAlerts} component.
 */
angular.module('alertServiceModule', [
        'smarteditServicesModule',
        'alertCollectionModule',
        'alertFactoryModule'
    ])

    /**
     * @ngdoc object
     * @name alertServiceModule.SE_ALERT_SERVICE_GATEWAY_ID
     * @description
     * The gateway UID used to proxy the {@link alertServiceModule.alertService alertService}<br />
     * Warning: This value must match the value of the <strong>same name<strong> in in the SmartEdit proxy version
     */
    .constant("SE_ALERT_SERVICE_GATEWAY_ID", 'SE_ALERT_SERVICE_GATEWAY_ID')

    /**
     * @ngdoc service
     * @name alertServiceModule.alertService
     *
     * @description
     * The alert service provides a simple interface for presenting alerts to the user.<br />
     * It acts as a facade to the lower level {@link alertFactoryModule.Alert Alert} and
     * {@link alertFactoryModule.alertFactory alertFactory}.
     *
     */
    .service('alertService', function(alertFactory, alertCollectionLegacySupport, gatewayProxy, SE_ALERT_SERVICE_GATEWAY_ID) {


        /**
         * @ngdoc method
         * @name alertServiceModule.alertService.showAlert
         * @methodOf alertServiceModule.alertService
         * @description
         * Displays an alert to the user. <br />
         * Convenience method to create an alert and call.show() on it immediately.
         * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS]
         * An {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
         */
        this.showAlert = function(alertConf) {
            var alert = alertFactory.createAlert(alertConf);
            alert.show();
        };

        /**
         * @ngdoc method
         * @name alertServiceModule.alertService.showInfo
         * @methodOf alertServiceModule.alertService
         * @description
         * Displays an alert to the user. <br />
         * Convenience method to create an alert and call.show() on it immediately.
         * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS]
         * An {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
         */
        this.showInfo = function(alertConf) {
            var alert = alertFactory.createInfo(alertConf);
            alert.show();
        };

        /**
         * @ngdoc method
         * @name alertServiceModule.alertService.showDanger
         * @methodOf alertServiceModule.alertService
         * @description
         * Displays an alert to the user. <br />
         * Convenience method to create an alert and call.show() on it immediately.
         * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS]
         * An {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
         */
        this.showDanger = function(alertConf) {
            var alert = alertFactory.createDanger(alertConf);
            alert.show();
        };

        /**
         * @ngdoc method
         * @name alertServiceModule.alertService.showWarning
         * @methodOf alertServiceModule.alertService
         * @description
         * Displays an alert to the user. <br />
         * Convenience method to create an alert and call.show() on it immediately.
         * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS]
         * An {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
         */
        this.showWarning = function(alertConf) {
            var alert = alertFactory.createWarning(alertConf);
            alert.show();
        };

        /**
         * @ngdoc method
         * @name alertServiceModule.alertService.showSuccess
         * @methodOf alertServiceModule.alertService
         * @description
         * Displays an alert to the user. <br />
         * Convenience method to create an alert and call.show() on it immediately.
         * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS]
         * An {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
         */
        this.showSuccess = function(alertConf) {
            var alert = alertFactory.createSuccess(alertConf);
            alert.show();
        };



        // ================= LEGACY FUNCTIONS =================

        /**
         * @ngdoc method
         * @name alertServiceModule.alertService.pushAlerts
         * @methodOf alertServiceModule.alertService
         * @description
         * LEGACY - please use one of the showXY alert methods instead.
         *
         */
        this.pushAlerts = function(alerts) {
            alerts.forEach(function(alert) {
                this.showAlert(alert);
            }.bind(this));
        }.bind(this);


        /**
         * @ngdocs method
         * @name alertServiceModule.alertService.removeAlertById
         * @methodOf alertServiceModule.alertService
         * @description
         * LEGACY - Should not be used, but still working for backwards compatibility.
         * <br />
         * To explicitly hide an alert, use the {@link alertFactoryModule.alertFactory alertFactory} instead of the
         * alertService. You can create an alert object there and .show() or .hide() it when needed.
         */
        this.removeAlertById = function(id) {
            alertCollectionLegacySupport.removeAlertById(id);
        };


        // ==== INIT ====
        gatewayProxy.initForService(this, null, SE_ALERT_SERVICE_GATEWAY_ID);

    });
