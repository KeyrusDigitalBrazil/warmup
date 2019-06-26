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
 * @name exponentialRetrylModule
 *
 * @description
 * This module provides the exponentialRetry service.
 */
angular.module('exponentialRetrylModule', [])
    /**
     * @ngdoc object
     * @name exponentialRetrylModule.object:EXPONENTIAL_RETRY_DEFAULT_SETTING
     * 
     * @description
     * The setting object to be used as default values for retry.
     */
    .constant("EXPONENTIAL_RETRY_DEFAULT_SETTING", {
        MAX_BACKOFF: 64000,
        MAX_ATTEMPT: 5,
        MIN_BACKOFF: 0
    })
    /**
     * @ngdoc service
     * @name exponentialRetrylModule.service:exponentialRetry
     * @description
     * When used by a retry strategy, this service could provide an exponential delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
     */
    .service('exponentialRetry', function(EXPONENTIAL_RETRY_DEFAULT_SETTING) {
        /**
         * @ngdoc method
         * @name exponentialRetrylModule.service:exponentialRetry#calculateNextDelay
         * @methodOf exponentialRetrylModule.service:exponentialRetry
         * 
         * @description
         * This method will calculate the next delay time.
         * 
         * @param {Number} attemptCount The current number of retry attempts
         * @param {Number =} maxBackoff The maximum delay between two retries
         * @param {Number =} minBackoff The minimum delay between two retries
         * 
         * @return {Number} The next delay value
         */
        this.calculateNextDelay = function(attemptCount, maxBackoff, minBackoff) {
            maxBackoff = maxBackoff || EXPONENTIAL_RETRY_DEFAULT_SETTING.MAX_BACKOFF;
            minBackoff = minBackoff || EXPONENTIAL_RETRY_DEFAULT_SETTING.MIN_BACKOFF;

            var waveShield = minBackoff + Math.random();

            var delay = Math.min(((Math.pow(2, attemptCount) * 1000) + waveShield), maxBackoff);
            return delay;
        };

        /**
         * @ngdoc method
         * @name exponentialRetrylModule.service:exponentialRetry#canRetry
         * @methodOf exponentialRetrylModule.service:exponentialRetry
         * 
         * @description
         * This method returns true if it is valid to perform another retry, otherwise, it returns false.
         * 
         * @param {Number} attemptCount The current number of retry attempts
         * @param {Number =} maxAttempt The maximum number of retry attempts
         * 
         * @return {Boolean} is valid to perform another retry?
         */
        this.canRetry = function(attemptCount, maxAttempt) {
            maxAttempt = maxAttempt || EXPONENTIAL_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
            return (attemptCount <= maxAttempt);
        };
    });
