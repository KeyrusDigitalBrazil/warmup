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

    angular.module('heartBeatServiceModule', ['alertServiceModule', 'smarteditServicesModule', 'translationServiceModule'])
        .constant('HEART_BEAT_PERIOD', 10000) // 10 seconds to receive a timeout
        .factory('heartBeatService', function($rootScope, $timeout, $translate, gatewayFactory, alertService, HEART_BEAT_PERIOD) {
            // Constants
            var HEART_BEAT_FAILURE_ALERT_ID = 'heartBeatFailure';
            var HEART_BEAT_GATEWAY_ID = 'heartBeatGateway';
            var HEART_BEAT_MSG_ID = 'heartBeat';

            var HeartBeatService = function() {
                this._heartBeatFailed = false;
                this._attachListeners();
            };

            HeartBeatService.prototype._attachListeners = function() {
                this._heartBeatGateway = gatewayFactory.createGateway(HEART_BEAT_GATEWAY_ID);

                // To perform the actual Heart Beat.
                this._heartBeatGateway.subscribe(HEART_BEAT_MSG_ID, function() {
                    // If there's a timer then the container is expecting a heart Beat, otherwise no need
                    // to reset .
                    if (this._heartBeatTimer) {
                        this.resetTimer(true);
                    }
                }.bind(this));

                // When navigating to a new page the timer is removed. It's the responsability of the page's
                // controller to re-enable the timer if they need a heart Beat check.
                // NOTE: $routeChangeStart is used instead of $routeChangeSuccess as the later is executed
                // after the page controller.
                $rootScope.$on('$routeChangeStart', function() {
                    this.resetTimer(false);
                }.bind(this));
            };

            HeartBeatService.prototype._failureCallback = function() {
                if (!this._heartBeatFailed) {
                    this._heartBeatFailed = true;
                    var translatedError = $translate.instant('se.heartbeat.failure');
                    alertService.showWarning({
                        id: HEART_BEAT_FAILURE_ALERT_ID,
                        message: translatedError,
                        closeable: false
                    });
                }
            };

            HeartBeatService.prototype.resetTimer = function(restartTimer) {
                if (this._heartBeatFailed) {
                    alertService.removeAlertById(HEART_BEAT_FAILURE_ALERT_ID);
                    this._heartBeatFailed = false;
                }

                if (this._heartBeatTimer) {
                    $timeout.cancel(this._heartBeatTimer);
                    this._heartBeatTimer = null;
                }

                if (restartTimer) {
                    this._heartBeatTimer = $timeout(this._failureCallback.bind(this), HEART_BEAT_PERIOD);
                }
            };

            return new HeartBeatService();
        });

})();
