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
 * @name RetryStrategyInterface
 */
angular.module('retryStrategyInterfaceModule', [])
    /**
     * @ngdoc service
     * @name retryStrategyInterfaceModule.service:RetryStrategyInterface
     */
    .factory('RetryStrategyInterface', function() {
        var Interface = function() {
            /**
             * @ngdoc method
             * @name retryStrategyInterfaceModule.service:RetryStrategyInterface#canRetry
             * @methodOf retryStrategyInterfaceModule.service:RetryStrategyInterface
             * 
             * @description
             * Function that must return a {Boolean} if the current request must be retried
             * 
             * @return {Boolean} true if the current request must be retried
             */
            this.canRetry = function() {};

            /**
             * @ngdoc method
             * @name retryStrategyInterfaceModule.service:RetryStrategyInterface#canRetry
             * @methodOf retryStrategyInterfaceModule.service:RetryStrategyInterface
             * 
             * @description
             * Function that returns the next delay time {Number}
             * 
             * @return {Number} delay the delay until the next retry
             */
            this.calculateNextDelay = function() {};
        };
        return Interface;
    });
