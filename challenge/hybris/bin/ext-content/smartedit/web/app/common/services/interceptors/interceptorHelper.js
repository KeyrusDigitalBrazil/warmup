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
angular.module('interceptorHelperModule', [])
    /**
     * @ngdoc service
     * @name interceptorHelperModule.service:interceptorHelper
     *
     * @description
     * Helper service used to handle request and response in interceptors
     */
    .service('interceptorHelper', function($q, $log) {

        return {

            _isEligibleForInterceptors: function(config) {
                return config && config.url && !/.+\.html$/.test(config.url);
            },

            _handle: function(chain, config, callback, isError) {
                try {
                    if (this._isEligibleForInterceptors(config)) {
                        return $q.when(callback());
                    } else {
                        if (isError) {
                            return $q.reject(chain);
                        } else {
                            return chain;
                        }
                    }
                } catch (e) {
                    $log.error("caught error in one of the interceptors", e);
                    if (isError) {
                        return $q.reject(chain);
                    } else {
                        return chain;
                    }
                }
            },

            /** 
             * @ngdoc method
             * @name interceptorHelperModule.service:interceptorHelper#methodsOf_handleRequest
             * @methodOf interceptorHelperModule.service:interceptorHelper
             *
             * @description
             * Handles body of an interceptor request
             * @param {object} config the request's config to be handled by an interceptor method
             * @param {callback} callback the callback function to handle the object. callback will either return a promise or the initial object.
             * @return {object} the config or a promise resolving or rejecting with the config
             */
            handleRequest: function(config, callback) {
                return this._handle(config, config, callback, false);
            },

            /** 
             * @ngdoc method
             * @name interceptorHelperModule.service:interceptorHelper#methodsOf_handleResponse
             * @methodOf interceptorHelperModule.service:interceptorHelper
             *
             * @description
             * Handles body of an interceptor response success
             * @param {object} response the response to be handled by an interceptor method
             * @param {callback} callback the callback function to handle the response. callback will either return a promise or the initial object.
             * @return {object} the response or a promise resolving or rejecting with the response
             */
            handleResponse: function(response, callback) {
                return this._handle(response, response.config, callback, false);
            },
            /** 
             * @ngdoc method
             * @name interceptorHelperModule.service:interceptorHelper#methodsOf_handleResponseError
             * @methodOf interceptorHelperModule.service:interceptorHelper
             *
             * @description
             * Handles body of an interceptor response error
             * @param {object} response the response to be handled by an interceptor method
             * @param {callback} callback the callback function to handle the response in error. callback will either return a promise or the initial object.
             * @return {object} the response or a promise resolving or rejecting with the response
             */
            handleResponseError: function(response, callback) {
                return this._handle(response, response.config, callback, true);
            }
        };
    });
