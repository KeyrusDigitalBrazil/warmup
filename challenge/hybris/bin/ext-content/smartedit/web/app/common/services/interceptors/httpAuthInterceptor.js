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
angular.module('httpAuthInterceptorModule', ['interceptorHelperModule', 'authenticationModule', 'smarteditServicesModule'])


    /**
     * @ngdoc service
     * @name httpAuthInterceptorModule.httpAuthInterceptor
     * 
     * @description
     * Makes it possible to perform global authentication by intercepting requests before they are forwarded to the server
     * and responses before they are forwarded to the application code.
     * 
     * The interceptors are service factories that are registered with the $httpProvider by adding them to the $httpProvider.interceptors array. 
     * The factory is called and injected with dependencies and returns the interceptor object, which contains the interceptor methods.
     */
    .factory('httpAuthInterceptor', function($log, $injector, interceptorHelper) {
        return {
            /** 
             * @ngdoc method
             * @name httpAuthInterceptorModule.httpAuthInterceptor#request
             * @methodOf httpAuthInterceptorModule.httpAuthInterceptor
             * 
             * @description
             * Interceptor method which gets called with a http config object, intercepts any request made using $http service.
             * A call to any REST resource will be intercepted by this method, which then adds an authentication token to the request
             * and then forwards it to the REST resource.
             * 
             * @param {Object} config - the http config object that holds the configuration information.
             */
            request: function(config) {
                return interceptorHelper.handleRequest(config, function() {
                    return $injector.get("authenticationService").filterEntryPoints(config.url).then(function(entryPoints) {
                        if (entryPoints && entryPoints.length) {
                            return $injector.get("storageService").getAuthToken(entryPoints[0]).then(function(authToken) {
                                $log.debug(['Intercepting request ' + (authToken ? 'adding access token' : 'no access token found'), config.url].join(' '));
                                if (authToken) {
                                    config.headers.Authorization = authToken.token_type + " " + authToken.access_token;
                                }
                                return config;
                            });
                        } else {
                            return config;
                        }
                    }, function() {
                        return config;
                    });
                });
            }
        };
    })
    .config(function($httpProvider) {
        $httpProvider.interceptors.push('httpAuthInterceptor');
    });
