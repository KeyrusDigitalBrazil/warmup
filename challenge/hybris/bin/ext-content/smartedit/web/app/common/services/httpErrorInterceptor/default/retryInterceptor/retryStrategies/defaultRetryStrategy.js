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
angular.module('defaultRetryStrategyModule', ['simpleRetrylModule'])
    .factory('defaultRetryStrategy', function(simpleRetry) {
        var Strategy = function() {
            this.firstFastRetry = true;
        };
        Strategy.prototype.canRetry = function() {
            return simpleRetry.canRetry(this.attemptCount);
        };
        Strategy.prototype.calculateNextDelay = function() {
            return simpleRetry.calculateNextDelay();
        };
        return Strategy;
    });
