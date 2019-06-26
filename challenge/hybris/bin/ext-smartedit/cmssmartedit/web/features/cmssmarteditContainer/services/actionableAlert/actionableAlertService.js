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
 * @name actionableAlertModule
 *
 * @description
 * This module defines the actionableAlert Angular component and its
 * associated constant and service. It uses the alertServiceModule to render an
 * Alert component structured around a description, an hyperlink label and a
 * custom controller.
 */
angular.module("actionableAlertModule", [
        "alertServiceModule"
    ])

    /**
     * @ngdoc object
     * @name actionableAlertModule.object:actionableAlertConstants
     *
     * @description
     * This object defines injectable Angular constant used by the {@link actionableAlertModule.service:actionableAlertService#methods_displayActionableAlert displayActionableAlert}
     * method to render the content of the actionableAlert.
     */
    .constant('actionableAlertConstants', {

        /**
         * @ngdoc property
         * @name ALERT_TEMPLATE {String}
         * @propertyOf actionableAlertModule.object:actionableAlertConstants
         *
         * @description
         * Lodash template defining the HTML content inserted within the
         * actionableAlert.
         * Below are listed the placeholders you can use which will get substituted
         * at run-time:
         *  - {String} description Text related to the associated cmsItem
         *  - {String} descriptionDetails Map of parameters passed to the translated description
         *  - {String} hyperlinkLabel Label for the hyperlink rendered within the
         *  - {String} hyperlinkDetails Map of parameters passed to the translated hyperlink
         *  contextual alert
         **/
        ALERT_TEMPLATE: "<div><p>{{ $alertInjectedCtrl.description | translate: $alertInjectedCtrl.descriptionDetails }}</p><div><a data-ng-click='alert.hide(); $alertInjectedCtrl.onClick();'>{{ $alertInjectedCtrl.hyperlinkLabel | translate: $alertInjectedCtrl.hyperlinkDetails }}</a></div></div>",

        /**
         * @ngdoc object
         * @name TIMEOUT_DURATION {Integer}
         * @propertyOf actionableAlertModule.object:actionableAlertConstants
         *
         * @description
         * The timeout duration of the cms alert item, in milliseconds.
         */
        TIMEOUT_DURATION: 20000,

        /**
         * @ngdoc object
         * @name ALERT_TYPES {Object}
         * @propertyOf actionableAlertModule.object:actionableAlertConstants
         * 
         * @description
         * Injectable angular constant<br/>
         * This object provides an enumeration with values for each of the possible types of alerts 
         * that can be opened with the actionableAlertService. Currently the available options are: 
         * INFO, ALERT, DANGER, WARNING and SUCCESS. 
         */
        ALERT_TYPES: {
            INFO: 'INFO',
            ALERT: 'ALERT',
            DANGER: 'DANGER',
            WARNING: 'WARNING',
            SUCCESS: 'SUCCESS'
        }
    })

    /**
     * @ngdoc service
     * @name actionableAlertModule.service:actionableAlertService
     *
     * @description
     * The actionableAlertService is used by external modules to render an
     * Alert structured around a description, an hyperlink label and a custom
     * controller.
     **/
    .service('actionableAlertService', function(
        alertService,
        actionableAlertConstants
    ) {

        var alertTypesMapping = {
            INFO: 'showInfo',
            ALERT: 'showAlert',
            DANGER: 'showDanger',
            WARNING: 'showWarning',
            SUCCESS: 'showSuccess'
        };

        /**
         * @ngdoc method
         * @name actionableAlertModule.service:actionableAlertService#displayActionableAlert
         * @methodOf actionableAlertModule.service:actionableAlertService
         *
         * @description
         *
         * @param {Object} alertContent A JSON object containing the specific configuration to be applied on the actionableAlert.
         * @param {Function} alertContent.controller Function defining Angular controller consumed by the contextual alert.
         * @param {String} alertContent.description Description displayed within the contextual alert.
         * @param {String} alertContent.hyperlinkLabel Label for the hyperlink displayed within the contextual alert.
         * @param {Number?} alertContent.timeoutDuration The timeout duration of the cms alert item, in milliseconds. If not provided, the default is used.
         * @param {String?} alertType A string representing the type of alert to display. The string must match one of the types
         * defined in {@link ALERT_TYPES}. If the alert type is null, or it doesn't match any of the valid types this method will default to 
         * INFO. 
         */
        this.displayActionableAlert = function(alertContent, alertType) {
            var alertMethodName = getMethodNameByAlertType(alertType);

            return alertService[alertMethodName]({
                closeable: true,
                controller: alertContent.controller,
                template: actionableAlertConstants.ALERT_TEMPLATE,
                timeout: alertContent.timeoutDuration || actionableAlertConstants.TIMEOUT_DURATION
            });
        };

        function getMethodNameByAlertType(alertType) {
            return (alertType && alertTypesMapping.hasOwnProperty(alertType)) ? alertTypesMapping[alertType] : alertTypesMapping.INFO;
        }
    });
