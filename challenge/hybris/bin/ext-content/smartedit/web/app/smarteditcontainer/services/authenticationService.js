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
angular
    .module('authenticationModule', [
        'authenticationInterfaceModule',
        'functionsModule',
        'smarteditCommonsModule',
        'resourceLocationsModule',
        'modalServiceModule',
        'ui.bootstrap',
        'yLoDashModule'
    ])
    /* 1) ngResource and ngRoute pulled transitively
     * 2) translationServiceModule is needed since the templates/modal/loginDialog.html template uses translate filter
     * Not declaring it will make UNIT fail.
     * 3) because of translationServiceModule pulling $http, one cannot wire here $modal, restServiceFactory or profileService
     * otherwise one ends up with cyclic reference. On then needs resort to dynamic 'runtime' injection via $injector.get
     */
    .factory('authenticationService', function(
        $q,
        $injector,
        $log,
        sanitize,
        AuthenticationServiceInterface,
        LANDING_PAGE_PATH,
        DEFAULT_AUTHENTICATION_ENTRY_POINT,
        DEFAULT_AUTH_MAP,
        DEFAULT_CREDENTIALS_MAP,
        gatewayProxy,
        sharedDataService,
        storageService,
        crossFrameEventService,
        EVENTS,
        getQueryString,
        convertToArray,
        copy,
        merge,
        isBlank,
        extend,
        lodash
    ) {
        var AuthenticationService = function() {
            this.reauthInProgress = {};
            this.gatewayId = 'authenticationService';
            gatewayProxy.initForService(this);
        };

        AuthenticationService = extend(AuthenticationServiceInterface, AuthenticationService);

        AuthenticationServiceInterface.prototype._launchAuth = function(authURIAndClientCredentials) {
            return $injector
                .get('languageService')
                .isInitialized()
                .then(function() {
                    return storageService.isInitialized().then(function(initialized) {
                        return $injector.get('modalService').open({
                            cssClasses: 'se-login-modal',
                            templateUrl: 'loginDialog.html',
                            controller: [
                                'modalManager',
                                function(modalManager) {
                                    modalManager.setShowHeaderDismiss(false);

                                    this.initialized = initialized;
                                    this.auth = {
                                        username: '',
                                        password: ''
                                    };
                                    this.authURI = authURIAndClientCredentials.authURI;
                                    storageService.removeAuthToken(this.authURI);
                                    this.authURIKey = btoa(this.authURI).replace(/=/g, '');

                                    this.submit = function(loginDialogForm) {
                                        var deferred = $q.defer();

                                        loginDialogForm.posted = true;
                                        loginDialogForm.errorMessage = '';
                                        loginDialogForm.failed = false;

                                        if (loginDialogForm.$valid) {
                                            var payload = copy(authURIAndClientCredentials.clientCredentials || {});
                                            payload.username = this.auth.username;
                                            payload.password = this.auth.password;
                                            payload.grant_type = 'password';

                                            $injector
                                                .get('$http')({
                                                    method: 'POST',
                                                    url: this.authURI,
                                                    headers: {
                                                        'Content-Type': 'application/x-www-form-urlencoded'
                                                    },
                                                    data: getQueryString(payload).replace('?', '')
                                                })
                                                .then(
                                                    function(response) {
                                                        var isMainEndPoint =
                                                            DEFAULT_AUTHENTICATION_ENTRY_POINT === this.authURI;
                                                        storageService.storeAuthToken(this.authURI, response.data);
                                                        $log.debug(
                                                            [
                                                                'API Authentication Success: ',
                                                                this.authURI,
                                                                ' status: ',
                                                                response.status
                                                            ].join(' ')
                                                        );
                                                        $injector
                                                            .get('sessionService')
                                                            .hasUserChanged()
                                                            .then(
                                                                // resolved
                                                                function(hasUserChanged) {
                                                                    modalManager.close({
                                                                        userHasChanged: hasUserChanged
                                                                    });
                                                                    if (isMainEndPoint) {
                                                                        $injector
                                                                            .get('sessionService')
                                                                            .setCurrentUsername();
                                                                    }
                                                                    deferred.resolve({
                                                                        userHasChanged: hasUserChanged
                                                                    });
                                                                },
                                                                // rejected
                                                                function(error) {
                                                                    $log.error(
                                                                        'Issue with sessionService.hasUserChanged(): ' +
                                                                        error
                                                                    );
                                                                    deferred.reject(error);
                                                                }
                                                            );
                                                    }.bind(this),
                                                    function(error) {
                                                        $log.debug(
                                                            [
                                                                'API Authentication Failure: ',
                                                                this.authURI,
                                                                ' status: ',
                                                                error.status
                                                            ].join(' ')
                                                        );
                                                        loginDialogForm.errorMessage =
                                                            (error.data && sanitize(error.data.error_description)) ||
                                                            'se.logindialogform.oauth.error.default';
                                                        loginDialogForm.failed = true;
                                                        deferred.reject();
                                                    }.bind(this)
                                                );
                                        } else {
                                            loginDialogForm.errorMessage =
                                                'se.logindialogform.username.and.password.required';
                                            deferred.reject();
                                        }
                                        return deferred.promise;
                                    };
                                }
                            ]
                        });
                    });
                });
        };

        AuthenticationServiceInterface.prototype.filterEntryPoints = function(resource) {
            return sharedDataService.get('authenticationMap').then(function(authenticationMap) {
                authenticationMap = merge(authenticationMap || {}, DEFAULT_AUTH_MAP);
                return convertToArray(authenticationMap)
                    .filter(function(entry) {
                        return new RegExp(entry.key, 'g').test(resource);
                    })
                    .map(function(element) {
                        return element.value;
                    });
            });
        };

        AuthenticationServiceInterface.prototype.isAuthEntryPoint = function(resource) {
            return sharedDataService.get('authenticationMap').then(function(authenticationMap) {
                var authEntryPoints = convertToArray(authenticationMap).map(function(element) {
                    return element.value;
                });
                return authEntryPoints.indexOf(resource) > -1 || resource === DEFAULT_AUTHENTICATION_ENTRY_POINT;
            });
        };
        /*
         * will try determine first relevant authentication entry point from authenticationMap and retrieve potential client credentials to be added on top of user credentials
         */
        AuthenticationServiceInterface.prototype._findAuthURIAndClientCredentials = function(resource) {
            return this.filterEntryPoints(resource).then(function(entryPoints) {
                return sharedDataService.get('credentialsMap').then(function(credentialsMap) {
                    credentialsMap = angular.extend(credentialsMap || {}, DEFAULT_CREDENTIALS_MAP);
                    var authURI = entryPoints[0];
                    return {
                        authURI: authURI,
                        clientCredentials: credentialsMap[authURI]
                    };
                });
            });
        };

        AuthenticationServiceInterface.prototype.authenticate = function(resource) {
            return this._findAuthURIAndClientCredentials(resource).then(
                function(authURIAndClientCredentials) {
                    return this._launchAuth(authURIAndClientCredentials).then(
                        function(data) {
                            crossFrameEventService
                                .publish(EVENTS.AUTHORIZATION_SUCCESS, {
                                    userHasChanged: data.userHasChanged
                                })
                                .finally(
                                    function() {
                                        (data.userHasChanged ?
                                            crossFrameEventService.publish(EVENTS.USER_HAS_CHANGED) :
                                            $q.when()
                                        ).then(function() {
                                            /**
                                             * We only need to reload when the user has changed and all authentication forms were closed.
                                             * There can be many authentication forms if some modules use different (from default one) end points.
                                             */
                                            var reauthInProcess = lodash
                                                .values(this.reauthInProgress)
                                                .some(function(inProcess) {
                                                    return inProcess;
                                                });

                                            if (data.userHasChanged && !reauthInProcess) {
                                                $injector.get('$route').reload();
                                            }
                                        });
                                    }.bind(this)
                                );
                            this.reauthInProgress[authURIAndClientCredentials.authURI] = false;
                        }.bind(this)
                    );
                }.bind(this)
            );
        };

        AuthenticationServiceInterface.prototype.logout = function() {
            // First, indicate the services that SmartEdit is logging out. This should give them the opportunity to clean up.
            // NOTE: This is not synchronous since some clean-up might be lengthy, and logging out should be fast.
            return crossFrameEventService.publish(EVENTS.LOGOUT, {}).finally(
                function(storage) {
                    storage.removeAllAuthTokens();
                    var $location = $injector.get('$location');
                    var currentLocation = $location.url();
                    if (isBlank(currentLocation) || currentLocation === LANDING_PAGE_PATH) {
                        $injector.get('$route').reload();
                    } else {
                        $location.url(LANDING_PAGE_PATH);
                    }
                }.bind(this, storageService)
            );
        };

        AuthenticationServiceInterface.prototype.isReAuthInProgress = function(entryPoint) {
            return $q.when(this.reauthInProgress[entryPoint] === true);
        };

        AuthenticationServiceInterface.prototype.setReAuthInProgress = function(entryPoint) {
            this.reauthInProgress[entryPoint] = true;
            return $q.when();
        };

        AuthenticationServiceInterface.prototype.isAuthenticated = function(url) {
            return this.filterEntryPoints(url).then(function(entryPoints) {
                var authURI = entryPoints && entryPoints[0];
                return storageService.getAuthToken(authURI).then(function(authToken) {
                    return !!authToken;
                });
            });
        };

        return new AuthenticationService();
    });
