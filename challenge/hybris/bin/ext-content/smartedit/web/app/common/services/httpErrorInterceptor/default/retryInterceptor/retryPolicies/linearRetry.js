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
 * @name linearRetrylModule
 *
 * @description
 * This module provides the linearRetry service.
 */
angular.module('linearRetrylModule', [])
    /**
     * @ngdoc object
     * @name linearRetrylModule.object:LINEAR_RETRY_DEFAULT_SETTING
     * 
     * @description
     * The setting object to be used as default values for retry.
     */
    .constant("LINEAR_RETRY_DEFAULT_SETTING", {
        MAX_ATTEMPT: 5,
        MAX_BACKOFF: 32000,
        MIN_BACKOFF: 0,
        RETRY_INTERVAL: 500
    })
    /**
     * @ngdoc service
     * @name linearRetrylModule.service:linearRetry
     * @description
     * When used by a retry strategy, this service could provide a linear delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
     */
    .service('linearRetry', function(LINEAR_RETRY_DEFAULT_SETTING) {
        /**
         * @ngdoc method
         * @name linearRetrylModule.service:linearRetry#calculateNextDelay
         * @methodOf linearRetrylModule.service:linearRetry
         * 
         * @description
         * This method will calculate the next delay time.
         * 
         * @param {Number} attemptCount The current number of retry attempts
         * @param {Number =} retryInterval The base interval between two retries
         * @param {Number =} maxBackoff The maximum delay between two retries
         * @param {Number =} minBackoff The minimum delay between two retries
         * 
         * @return {Number} The next delay value
         */
        this.calculateNextDelay = function(attemptCount, retryInterval, maxBackoff, minBackoff) {
            maxBackoff = maxBackoff || LINEAR_RETRY_DEFAULT_SETTING.MAX_BACKOFF;
            minBackoff = minBackoff || LINEAR_RETRY_DEFAULT_SETTING.MIN_BACKOFF;
            retryInterval = retryInterval || LINEAR_RETRY_DEFAULT_SETTING.RETRY_INTERVAL;

            var waveShield = minBackoff + Math.random();
            var delay = Math.min(attemptCount * retryInterval + waveShield, maxBackoff);
            return delay;
        };

        /**
         * @ngdoc method
         * @name linearRetrylModule.service:linearRetry#canRetry
         * @methodOf linearRetrylModule.service:linearRetry
         * 
         * @description
         * This method returns true if it is valid to perform another retry, otherwise, it returns false.
         * 
         * @param {Number} attemptCount The current number of retry attempts
         * @param {Number =} maxRetry The maximum number of retry attempts
         * 
         * @return {Boolean} is valid to perform another retry?
         */
        this.canRetry = function(attemptCount, maxAttempt) {
            maxAttempt = maxAttempt || LINEAR_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
            return (attemptCount <= maxAttempt);
        };
    });
