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
 * @name retryInterceptorModule
 *
 * @description
 * This module provides the functionality to retry failing xhr requests through the registration of retry strategies.
 */
angular.module('retryInterceptorModule', ['functionsModule', 'smarteditCommonsModule', 'httpMethodPredicatesModule', 'httpErrorPredicatesModule', 'operationContextPredicatesModule', 'defaultRetryStrategyModule', 'exponentialRetryStrategyModule', 'linearRetryStrategyModule', 'alertServiceModule'])
    /**
     * @ngdoc service
     * @name retryInterceptorModule.service:retryInterceptor
     *
     * @description
     * The retryInterceptor provides the functionality to register a set of predicates with their associated retry strategies.
     * Each time an HTTP request fails, the service try to find a matching retry strategy for the given response.
     */
    .factory('retryInterceptor', function($http, $q, $timeout, $translate, operationContextService, alertService, OPERATION_CONTEXT, isAllTruthy, isAnyTruthy, noInternetConnectionErrorPredicate, readPredicate, updatePredicate, clientErrorPredicate, timeoutErrorPredicate, serverErrorPredicate, retriableErrorPredicate, operationContextInteractivePredicate, operationContextNonInteractivePredicate, operationContextCMSPredicate, operationContextToolingPredicate, defaultRetryStrategy, exponentialRetryStrategy, linearRetryStrategy) {
        var TRANSLATE_NAMESPACE = 'se.gracefuldegradation.';

        // Array<obj>
        // obj.predicate {Function}
        // obj.retryStrategy {Function}
        var predicatesRegistry = [];

        /**
         * Find a matching strategy for the given response and (optional) operationContext
         * If not provided, the default operationContext is OPERATION_CONTEXT.INTERACTIVE
         * 
         * @param {Object} response The http response object
         * 
         * @return {Function} The matching retryStrategy
         */
        var findMatchingStrategy = function(response) {
            var operationContext = operationContextService.findOperationContext(response.config.url) || OPERATION_CONTEXT.INTERACTIVE;
            var matchStrategy = predicatesRegistry.find(function(predicateObj) {
                return predicateObj.predicate(response, operationContext);
            });
            return matchStrategy ? matchStrategy.retryStrategy : null;
        };

        var handleRetry = function(response) {
            var retryStrategy = response.config.retryStrategy;
            retryStrategy.attemptCount++;
            if (retryStrategy.canRetry()) {
                var defer = $q.defer();
                var delay = retryStrategy.firstFastRetry ? 0 : retryStrategy.calculateNextDelay();
                retryStrategy.firstFastRetry = false;
                $timeout(function() {
                    defer.resolve($http(response.config));
                }, delay);
                return defer.promise;
            } else {
                alertService.showDanger({
                    message: $translate.instant(TRANSLATE_NAMESPACE + 'somethingwrong')
                });
                return $q.reject(response);
            }
        };

        var retryInterceptor = {
            predicate: function(response) {
                return findMatchingStrategy(response) !== null;
            },

            responseError: function(response) {
                if (response.config.retryStrategy) {
                    return $q.when(handleRetry(response));
                } else {
                    var StrategyHolder = findMatchingStrategy(response);
                    if (StrategyHolder) {
                        alertService.showWarning({
                            message: $translate.instant(TRANSLATE_NAMESPACE + 'stillworking')
                        });
                        var strategyInstance = new StrategyHolder();
                        strategyInstance.attemptCount = 0;
                        response.config.retryStrategy = strategyInstance;
                        return $q.when(handleRetry(response));
                    } else {
                        return $q.reject(response);
                    }
                }
            },

            /**
             * @ngdoc method
             * @name retryInterceptorModule.service:retryInterceptor#register
             * @methodOf retryInterceptorModule.service:retryInterceptor
             *
             * @description
             * Register a new predicate with it's associated strategyHolder.
             *
             * @param {Function} predicate This function takes the 'response' {Object} argument and an (optional) operationContext {String}. This function must return a Boolean that is true if the given response match the predicate.
             * @param {Function} strategyHolder This function will be instanciated at run-time. See {@link retryStrategyInterfaceModule.RetryStrategyInterface RetryStrategyInterface}.
             * 
             * @return {Object} retryInterceptor The retryInterceptor service.
             *
             * @example
             * ```js
             * angular.module('customInterceptorExample', ['retryInterceptorModule', 'smarteditCommonsModule'])
             *  .controller('ExampleController', function($q, retryInterceptor, OPERATION_CONTEXT) {
             *      var customPredicate = function(httpObj, operationContext) {
             *          return httpObj.status === 500 && operationContext === OPERATION_CONTEXT.TOOLING;
             *      };
             *      var StrategyHolder = function() {
             *          // set the firstFastRetry value to true for the retry made immediately only for the very first retry (subsequent retries will remain subject to the calculateNextDelay response)
             *          this.firstFastRetry = true;
             *      };
             *      StrategyHolder.prototype.canRetry = function() {
             *          // this function must return a {Boolean} if the given request must be retried.
             *          // use this.attemptCount value to determine if the function should return true or false
             *      };
             *      StrategyHolder.prototype.calculateNextDelay = function() {
             *          // this function must return the next delay time {Number}
             *          // use this.attemptCount value to determine the next delay value
             *      };
             *      retryInterceptor.register(customPredicate, StrategyHolder);
             *  });
             * ```
             */
            register: function(predicate, strategyHolder) {
                if (typeof predicate !== 'function') {
                    throw new Error('retryInterceptor.register error: predicate must be a function');
                }
                if (typeof strategyHolder !== 'function') {
                    throw new Error('retryInterceptor.register error: strategyHolder must be a function');
                }
                predicatesRegistry.unshift({
                    predicate: predicate,
                    retryStrategy: strategyHolder
                });
                return this;
            }
        };

        retryInterceptor
            .register(noInternetConnectionErrorPredicate, exponentialRetryStrategy)
            .register(isAnyTruthy(clientErrorPredicate, timeoutErrorPredicate), defaultRetryStrategy)
            .register(isAllTruthy(operationContextInteractivePredicate, retriableErrorPredicate), defaultRetryStrategy)
            .register(isAllTruthy(readPredicate, retriableErrorPredicate), defaultRetryStrategy)
            .register(serverErrorPredicate, exponentialRetryStrategy)
            .register(isAllTruthy(operationContextNonInteractivePredicate, retriableErrorPredicate), exponentialRetryStrategy)
            .register(isAllTruthy(operationContextCMSPredicate, timeoutErrorPredicate, updatePredicate), exponentialRetryStrategy)
            .register(isAllTruthy(operationContextToolingPredicate, timeoutErrorPredicate, updatePredicate), linearRetryStrategy);

        return retryInterceptor;
    });
