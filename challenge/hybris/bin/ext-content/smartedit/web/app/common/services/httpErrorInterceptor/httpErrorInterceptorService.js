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
 * @name httpErrorInterceptorServiceModule
 * 
 * @description
 * This module provides the functionality to add custom HTTP error interceptors.
 * Interceptors are used to execute code each time an HTTP request fails.
 */
angular.module('httpErrorInterceptorServiceModule', ['interceptorHelperModule'])
    .factory('httpErrorInterceptor', function(httpErrorInterceptorService, interceptorHelper) {
        return {
            responseError: function(response) {
                return interceptorHelper.handleResponseError(response, function() {
                    return httpErrorInterceptorService.responseError(response);
                });
            }
        };
    })
    /**
     * @ngdoc service
     * @name httpErrorInterceptorServiceModule.service:httpErrorInterceptorService
     * 
     * @description
     * The httpErrorInterceptorService provides the functionality to add custom HTTP error interceptors.
     * An interceptor can be an {Object} or an Angular Factory and must be represented by a pair of functions:
     * - predicate(response) {Function} that must return true if the response is associated to the interceptor. Important: The predicate must be designed to fulfill a specific function. It must not be defined for generic use.
     * - responseError(response) {Function} function called if the current response error matches the predicate. It must return a {Promise} with the resolved or rejected response.
     * 
     * Each time an HTTP request fails, the service iterates through all registered interceptors. It sequentially calls the responseError function for all interceptors that have a predicate returning true for the current response error. If an interceptor modifies the response, the next interceptor that is called will have the modified response.
     * The last interceptor added to the service will be the first interceptor called. This makes it possible to override default interceptors.
     * If an interceptor resolves the response, the service service stops the iteration.
     */
    .service('httpErrorInterceptorService', function($injector, $log, $q) {
        this._errorInterceptors = [];
        this._interceptorsDeferred = $q.defer();

        /**
         * @ngdoc method
         * @name httpErrorInterceptorServiceModule.service:httpErrorInterceptorService#addInterceptor
         * @methodOf httpErrorInterceptorServiceModule.service:httpErrorInterceptorService
         * 
         * @description
         * Add a new error interceptor
         * 
         * @param {Object|String} interceptor The interceptor {Object} or angular Factory
         * 
         * @returns {Function} Function to call to unregister the interceptor from the service
         * 
         * @example
         * ```js
         * angular.module('customInterceptorExample', ['httpErrorInterceptorServiceModule', 'alertServiceModule'])
         *  .controller('ExampleController', function($q, httpErrorInterceptorService, alertService) {
         *      // Add a new interceptor with an object:
         *      var unregisterCustomInterceptor = httpErrorInterceptorService.addInterceptor({
         *          predicate: function(response) {
         *              return response.status === 400;
         *          },
         *          responseError: function(response) {
         *              alertService.showDanger({
         *                  message: response.message
         *              });
         *              return $q.reject(response);
         *          }
         *      });
         *
         *      // Add an interceptor with an angular Factory:
         *      var unregisterCustomInterceptor = httpErrorInterceptorService.addInterceptor('customErrorInterceptor'); 
         * 
         *      // Unregister the interceptor:
         *      unregisterCustomInterceptor();
         *  });
         * ```
         */
        this.addInterceptor = function(interceptor) {
            if (typeof interceptor === 'string') {
                if ($injector.has(interceptor)) {
                    interceptor = $injector.get(interceptor);
                } else {
                    throw "httpErrorInterceptorService.interceptor.undefined";
                }
            }

            this._validateInterceptor(interceptor);
            this._errorInterceptors.unshift(interceptor);

            return function() {
                this._errorInterceptors.splice(this._errorInterceptors.indexOf(interceptor), 1);
            }.bind(this);
        };

        this.responseError = function(response) {
            var matchingErrorInterceptors = this._errorInterceptors.filter(function(errorInterceptor) {
                return errorInterceptor.predicate(response) === true;
            });
            this._interceptorsDeferred = $q.defer();
            if (matchingErrorInterceptors.length) {
                this._iterateErrorInterceptors(angular.copy(response), matchingErrorInterceptors);
            } else {
                this._interceptorsDeferred.reject(response);
            }
            return this._interceptorsDeferred.promise;
        };

        this._iterateErrorInterceptors = function(response, interceptors, idx) {
            if (idx === interceptors.length) {
                this._interceptorsDeferred.reject(response);
            } else {
                var def = this._interceptorsDeferred;
                var iterateFn = this._iterateErrorInterceptors.bind(this);
                idx = idx || 0;
                $q.when(interceptors[idx].responseError(response)).then(function(interceptedResponse) {
                    def.resolve(interceptedResponse);
                }, function(interceptedResponse) {
                    iterateFn(interceptedResponse, interceptors, ++idx);
                });
            }
        };

        /**
         * @ignore
         * Validate if the provided interceptor respects the Interface (predicate and responseError functions are mandatory).
         * @param {Object|String} interceptor The interceptor {Object} or angular Factory
         */
        this._validateInterceptor = function(interceptor) {
            if (typeof interceptor.predicate !== 'function') {
                throw new Error('httpErrorInterceptorService.addInterceptor.error.interceptor.has.no.predicate');
            }
            if (typeof interceptor.responseError !== 'function') {
                throw new Error('httpErrorInterceptorService.addInterceptor.error.interceptor.has.no.responseError');
            }
        };
    })
    .config(function($httpProvider) {
        $httpProvider.interceptors.push('httpErrorInterceptor');
    });
