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
(function() {

    var INFO = 'info';

    /**
     * @ngdoc overview
     * @name alertFactoryModule
     * @description
     * <h1>The Alert service module</h1>
     * The alert service module provides a centralized service to display alerts to users, from the container
     * application or the iframed application to users
     * <br />
     * The alerts are displayed using the {@link systemAlertsModule.systemAlerts systemAlerts} component.
     */
    angular.module('alertFactoryModule', [
            'alertCollectionFacadesModule',
            'yLoDashModule',
            'functionsModule'
        ])

        /**
         * @ngdoc object
         * @name alertFactoryModule.object:SE_ALERT_SERVICE_TYPES
         * @description
         * The available Alert types that can be provided in the type property of an AlertConfig
         */
        .constant("SE_ALERT_SERVICE_TYPES", {
            /**
             * @ngdoc property
             * @name INFO
             * @propertyOf alertFactoryModule.object:SE_ALERT_SERVICE_TYPES
             *
             * @description
             * Presents general information to the user.
             **/
            INFO: INFO,

            /**
             * @ngdoc property
             * @name SUCCESS
             * @propertyOf alertFactoryModule.object:SE_ALERT_SERVICE_TYPES
             *
             * @description
             * Presents information about a successful action or result to the user.
             **/
            SUCCESS: "success",

            /**
             * @ngdoc property
             * @name WARNING
             * @propertyOf alertFactoryModule.object:SE_ALERT_SERVICE_TYPES
             *
             * @description
             * Presents information about potentially risky actions or actions that could result in harmful consequences.
             **/
            WARNING: "warning",

            /**
             * @ngdoc property
             * @name DANGER
             * @propertyOf alertFactoryModule.object:SE_ALERT_SERVICE_TYPES
             *
             * @description
             * Presents errors or problems encountered to the user.
             **/
            DANGER: "danger" // Using the same states as the ones used by Bootstrap, but we could use our own instead? (i.e. 'error' instead of 'danger')
        })

        /**
         * @ngdoc object
         * @name alertFactoryModule.object:AlertConfig
         * @description
         * Configuration object for creating/displaying alerts.
         */
        /**
         * @ngdoc property
         * @name type
         * @propertyOf alertFactoryModule.object:AlertConfig
         * @description
         * {String} default: {@link alertFactoryModule.object:SE_ALERT_SERVICE_TYPES SE_ALERT_SERVICE_TYPES.INFO} <br />
         * One of {@link alertFactoryModule.object:SE_ALERT_SERVICE_TYPES SE_ALERT_SERVICE_TYPES}
         */
        /**
         * @ngdoc property
         * @name message
         * @propertyOf alertFactoryModule.object:AlertConfig
         * @description
         * {String} <br />
         * The alert i18n message to be displayed to the user
         */
        /**
         * @ngdoc property
         * @name messagePlaceholders
         * @propertyOf alertFactoryModule.object:AlertConfig
         * @description
         * {Object} <br />
         * An object containing values for placeholders in he i18n message for dynamized values
         */
        /**
         * @ngdoc property
         * @name template
         * @propertyOf alertFactoryModule.object:AlertConfig
         * @description
         * {String} <br />
         * The alert inline template to be displayed to the user
         */
        /**
         * @ngdoc property
         * @name templateUrl
         * @propertyOf alertFactoryModule.object:AlertConfig
         * @description
         * {String} <br />
         * The alert template file to be displayed to the user
         */
        /**
         * @ngdoc property
         * @name closeable
         * @propertyOf alertFactoryModule.object:AlertConfig
         * @description
         * {Boolean} default: true <br />
         * If true, it allows the user to manually dismiss the displayed alert.
         */
        /**
         * @ngdoc property
         * @name timeout
         * @propertyOf alertFactoryModule.object:AlertConfig
         * @description
         * {Number} default: 3000 <br />
         * If greater than 0, the alert will automatically be dismissed after the timeout.<br />
         * Setting closeabled to false, will not prevent this alert from being auto-dismissed.
         */

        /**
         * @ngdoc object
         * @name alertFactoryModule.object:SE_ALERT_DEFAULTS
         * @description
         * The default values used for alerts.
         * See {@link alertFactoryModule.object:AlertConfig AlertConfig} for more details.
         */
        .constant("SE_ALERT_DEFAULTS", {
            type: INFO,
            message: '',
            closeable: true,
            timeout: 3000
        })

        /**
         * @ngdoc service
         * @name alertFactoryModule.alertFactory
         * @description
         * # alertFactory
         * The alertFactory allows you to create {@link alertFactoryModule.Alert Alert} instances.<br />
         * When possible, it is better to use {@link alertServiceModule.alertService alertService} to show alerts.<br />
         * This factory is useful when one of the {@link alertFactoryModule.Alert Alert} methods is needed, like
         * hide() or isDisplayed(), or if you want to create a single instance and hide/show when necessary.
         **/
        .factory('alertFactory', function($q, $timeout, $log, sanitize, alertCollectionServiceFacade, lodash, SE_ALERT_SERVICE_TYPES, SE_ALERT_DEFAULTS) {

            /**
             *  Because pre-refactoring we used only a 'successful' property, instead of alert types
             */
            function fixLegacyAlert(legacyAlertConf) {
                if (legacyAlertConf.type) {
                    $log.warn('alertService validation warning: alert contains both legacy successful ' +
                        'property and an alert type for alert: ', legacyAlertConf);
                } else {
                    if (typeof legacyAlertConf.successful !== "boolean") {
                        $log.warn('alertService validation warning: legacyAlertConf.successful not a boolean value for alert: ', legacyAlertConf);
                    }
                    legacyAlertConf.type = legacyAlertConf.successful ? SE_ALERT_SERVICE_TYPES.SUCCESS : SE_ALERT_SERVICE_TYPES.DANGER;
                }
                delete legacyAlertConf.successful;
            }

            /**
             *  Alert conf validation
             */
            function validateAlertConfig(alertConf) {
                if (typeof alertConf.successful !== "undefined") {
                    fixLegacyAlert(alertConf);
                }
                if (!alertConf.message && !alertConf.template && !alertConf.templateUrl) {
                    $log.warn('alertService._validateAlertConfig - alert must contain at least a message, template, or templateUrl property', alertConf);
                }
                if (alertConf.messagePlaceholders && !angular.isObject(alertConf.messagePlaceholders)) {
                    throw new Error('alertService._validateAlertConfig - property messagePlaceholders should be an object');
                }
                if ((alertConf.message && (alertConf.template || alertConf.templateUrl)) ||
                    (alertConf.template && (alertConf.message || alertConf.templateUrl)) ||
                    (alertConf.templateUrl && (alertConf.message || alertConf.template))) {

                    throw new Error('alertService._validateAlertConfig - only one template type is allowed for the alert: message, template, or templateUrl');
                }
            }

            /**
             * Sanitizes the template and message.
             */
            function sanitizeTemplates(alertConf) {
                if (alertConf.message) {
                    alertConf.message = sanitize(alertConf.message);
                } else if (alertConf.template) {
                    alertConf.template = alertConf.template;
                }
            }

            /**
             * @ngdoc service
             * @name alertFactoryModule.Alert
             * @description
             * # Alert
             * Represents an alert that can display a message to the user. Only one
             * type of template is allowed (message, template, or templateUrl).
             * A custom controller can be passed as a parameter to the alertConf
             * object and consumed by the Alert template under the alias
             * '$alertInjectedController'.
             *
             * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS]
             * An {@link alertFactoryModule.object:AlertConfig AlertConfig} object
             *
             **/
            function Alert(alertConf) {
                alertConf = alertConf || {};
                validateAlertConfig(alertConf);
                sanitizeTemplates(alertConf);

                // copy defaults and merge properties onto this
                lodash.defaultsDeep(this, alertConf, lodash.cloneDeep(SE_ALERT_DEFAULTS));

                this._displayed = false;
            }

            /**
             * @ngdoc method
             * @name alertFactoryModule.Alert.show
             * @methodOf alertFactoryModule.Alert
             * @description
             * Displays the alert to the user. <br />
             * <br />
             * Note: This is only successful if the {@link systemAlertsModule.systemAlerts systemAlerts} component (single instance)
             * is in the application DOM. SmartEdit is packaged with one embedded in the application root HTML, therefore this shouldn't be an issue.
             **/
            Alert.prototype.show = function() {
                if (!this._displayed) {
                    alertCollectionServiceFacade.addAlert(this);

                    this._deferred = $q.defer();
                    this.promise = this._deferred.promise;
                    this._displayed = true;

                    if (this.timeout && this.timeout > 0) {
                        this.timer = $timeout(function() {
                                this.hide(true);
                            }.bind(this),
                            this.timeout);
                    }
                }
            };

            /**
             * @ngdoc method
             * @name alertFactoryModule.Alert.hide
             * @methodOf alertFactoryModule.Alert
             * @description
             * Hides the alert if it is currently being displayed to the user.
             **/
            Alert.prototype.hide = function(timedOut) {
                if (this._displayed) {
                    alertCollectionServiceFacade.removeAlert(this);

                    this._displayed = false;
                    if (this.timer) {
                        $timeout.cancel(this.timer);
                        delete this.timer;
                    }
                    if (typeof timedOut === "undefined") {
                        timedOut = false;
                    }
                    this._deferred.resolve(timedOut);
                }
            };

            /**
             * @ngdoc method
             * @name alertFactoryModule.Alert.isDisplayed
             * @methodOf alertFactoryModule.Alert
             * @returns {Boolean} True while the alert is displayed to the user, otherwise false.
             **/
            Alert.prototype.isDisplayed = function() {
                return this._displayed;
            };

            /**
             * Allow the user to pass a str param or config object
             * Will convert a str param to { message: str }
             */
            function getAlertConfigFromStringOrConfig(strOrConf) {
                if (typeof strOrConf === "string") {
                    return {
                        message: strOrConf
                    };
                }
                return strOrConf;
            }

            return {

                /**
                 * @ngdoc method
                 * @name alertFactoryModule.alertFactory.createAlert
                 * @methodOf alertFactoryModule.alertFactory
                 * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS] An
                 * {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
                 * @returns {alertFactoryModule.Alert} An {@link alertFactoryModule.Alert Alert} instance
                 **/
                createAlert: function(alertConf) {
                    alertConf = getAlertConfigFromStringOrConfig(alertConf);
                    return new Alert(alertConf);
                },

                /**
                 * @ngdoc method
                 * @name alertFactoryModule.alertFactory.createInfo
                 * @methodOf alertFactoryModule.alertFactory
                 * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS] An
                 * {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
                 * @returns {alertFactoryModule.Alert} An {@link alertFactoryModule.Alert Alert} instance with type set to INFO
                 **/
                createInfo: function(alertConf) {
                    alertConf = getAlertConfigFromStringOrConfig(alertConf);
                    alertConf.type = SE_ALERT_SERVICE_TYPES.INFO;
                    return new Alert(alertConf);
                },

                /**
                 * @ngdoc method
                 * @name alertFactoryModule.alertFactory.createDanger
                 * @methodOf alertFactoryModule.alertFactory
                 * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS] An
                 * {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
                 * @returns {alertFactoryModule.Alert} An {@link alertFactoryModule.Alert Alert} instance with type set to DANGER
                 **/
                createDanger: function(alertConf) {
                    alertConf = getAlertConfigFromStringOrConfig(alertConf);
                    alertConf.type = SE_ALERT_SERVICE_TYPES.DANGER;
                    return new Alert(alertConf);
                },

                /**
                 * @ngdoc method
                 * @name alertFactoryModule.alertFactory.createWarning
                 * @methodOf alertFactoryModule.alertFactory
                 * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS] An
                 * {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
                 * @returns {alertFactoryModule.Alert} An {@link alertFactoryModule.Alert Alert} instance with type set to WARNING
                 **/
                createWarning: function(alertConf) {
                    alertConf = getAlertConfigFromStringOrConfig(alertConf);
                    alertConf.type = SE_ALERT_SERVICE_TYPES.WARNING;
                    return new Alert(alertConf);
                },

                /**
                 * @ngdoc method
                 * @name alertFactoryModule.alertFactory.createSuccess
                 * @methodOf alertFactoryModule.alertFactory
                 * @param {Object=} [alertConf=alertFactoryModule.SE_ALERT_DEFAULTS] An
                 * {@link alertFactoryModule.object:AlertConfig AlertConfig} object OR a message string
                 * @returns {alertFactoryModule.Alert} An {@link alertFactoryModule.Alert Alert} instance with type set to SUCCESS
                 **/
                createSuccess: function(alertConf) {
                    alertConf = getAlertConfigFromStringOrConfig(alertConf);
                    alertConf.type = SE_ALERT_SERVICE_TYPES.SUCCESS;
                    return new Alert(alertConf);
                }
            };

        });
})();
