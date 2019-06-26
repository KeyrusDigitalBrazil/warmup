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
 * @name simpleRetrylModule
 *
 * @description
 * This module provides the simpleRetry service.
 */
angular.module('simpleRetrylModule', [])
    /**
     * @ngdoc object
     * @name simpleRetrylModule.object:SIMPLE_RETRY_DEFAULT_SETTING
     * 
     * @description
     * The setting object to be used as default values for retry.
     */
    .constant("SIMPLE_RETRY_DEFAULT_SETTING", {
        MAX_ATTEMPT: 5,
        MIN_BACKOFF: 0,
        RETRY_INTERVAL: 500
    })
    /**
     * @ngdoc service
     * @name simpleRetrylModule.service:simpleRetry
     * @description
     * When used by a retry strategy, this service could provide a simple fixed delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
     */
    .service('simpleRetry', function(SIMPLE_RETRY_DEFAULT_SETTING) {
        /**
         * @ngdoc method
         * @name simpleRetrylModule.service:simpleRetry#calculateNextDelay
         * @methodOf simpleRetrylModule.service:simpleRetry
         * 
         * @description
         * This method will calculate the next delay time.
         * 
         * @param {Number =} retryInterval The base interval between two retries
         * @param {Number =} minBackoff The minimum delay between two retries
         * 
         * @return {Number} The next delay value
         */
        this.calculateNextDelay = function(retryInterval, minBackoff) {
            minBackoff = minBackoff || SIMPLE_RETRY_DEFAULT_SETTING.MIN_BACKOFF;
            retryInterval = retryInterval || SIMPLE_RETRY_DEFAULT_SETTING.RETRY_INTERVAL;

            var waveShield = minBackoff + Math.random();
            var delay = retryInterval + waveShield;
            return delay;
        };

        /**
         * @ngdoc method
         * @name simpleRetrylModule.service:simpleRetry#canRetry
         * @methodOf simpleRetrylModule.service:simpleRetry
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
            maxAttempt = maxAttempt || SIMPLE_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
            return (attemptCount <= maxAttempt);
        };
    });
