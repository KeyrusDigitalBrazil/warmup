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
angular.module('unauthorizedErrorInterceptorModule', [
        'authenticationModule',
        'smarteditServicesModule'
    ])

    /**
     * @ngdoc service
     * @name unauthorizedErrorInterceptorModule.service:unauthorizedErrorInterceptor
     * @description
     * Used for HTTP error code 401 (Forbidden). It will display the login modal.
     */
    .factory('unauthorizedErrorInterceptor', function($injector, $q, authenticationService, WHO_AM_I_RESOURCE_URI) {

        var promisesToResolve = {}; // key: auth entry point, value: array of deferred
        var rejectedUrls = [
            WHO_AM_I_RESOURCE_URI
        ];
        var isUrlNotRejected = function(url) {
            return !rejectedUrls.some(function(rejectedUrl) {
                return url.indexOf(rejectedUrl) === 0;
            });
        };

        return {
            predicate: function(response) {
                return response.status === 401 && ((response.config && response.config.url) ? isUrlNotRejected(response.config.url) : true);
            },
            responseError: function(response) {
                var deferred = $q.defer();
                authenticationService.isAuthEntryPoint(response.config.url).then(function(isAuthEntryPoint) {
                    if (!isAuthEntryPoint) {
                        authenticationService.filterEntryPoints(response.config.url).then(function(entryPoints) {
                            var entryPoint = entryPoints[0];
                            promisesToResolve[entryPoint] = promisesToResolve[entryPoint] || [];
                            promisesToResolve[entryPoint].push(deferred);
                            authenticationService.isReAuthInProgress(entryPoint).then(function(isReAuthInProgress) {
                                if (!isReAuthInProgress) {
                                    authenticationService.setReAuthInProgress(entryPoint).then(function() {
                                        authenticationService.authenticate(response.config.url).then(function() {
                                            angular.forEach(promisesToResolve[this], function(def) {
                                                def.resolve();
                                            });
                                            promisesToResolve[this] = [];
                                        }.bind(entryPoint), function() {
                                            angular.forEach(promisesToResolve[this], function(def) {
                                                def.reject();
                                            });
                                            promisesToResolve[this] = [];
                                        }.bind(entryPoint));
                                    });
                                }
                            }.bind(this));
                        });
                    } else {
                        deferred.reject(response);
                    }
                });

                return deferred.promise.then(function() {
                    return $injector.get('$http')(response.config);
                });
            }
        };
    });
