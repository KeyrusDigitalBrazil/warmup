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
describe('outer AuthenticationService', function() {
    var authenticationService,
        sessionService,
        storageService,
        sharedDataService,
        controllerContext,
        $httpBackend,
        modalService,
        modalManager,
        languageService,
        parseQuery,
        $q,
        $location,
        $route,
        $timeout,
        systemEventService,
        EVENTS,
        $rootScope,
        gatewayProxy,
        DEFAULT_AUTHENTICATION_ENTRY_POINT = 'defaultAuthEntryPoint';

    beforeEach(angular.mock.module("ngMock"));

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {

        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {
        systemEventService = jasmine.createSpyObj('systemEventService', ['publishAsync']);
        $provide.value('systemEventService', systemEventService);

        EVENTS = {
            LOGOUT: 'some logout event'
        };
        $provide.value('EVENTS', EVENTS);
    }));

    beforeEach(angular.mock.module("authenticationModule", function($provide) {

        modalService = jasmine.createSpyObj('modalService', ['open']);
        $provide.value('modalService', modalService);

        languageService = jasmine.createSpyObj('languageService', ['isInitialized']);
        $provide.value('languageService', languageService);

        sessionService = jasmine.createSpyObj('sessionService', ['hasUserChanged', 'setCurrentUsername', 'resetCurrentUserData']);
        $provide.value('sessionService', sessionService);

        $provide.constant('DEFAULT_AUTHENTICATION_ENTRY_POINT', DEFAULT_AUTHENTICATION_ENTRY_POINT);
        $provide.constant('DEFAULT_AUTH_MAP', {
            'api3': DEFAULT_AUTHENTICATION_ENTRY_POINT
        });
        $provide.constant('DEFAULT_AUTHENTICATION_CLIENT_ID', 'smartedit');

        $location = jasmine.createSpyObj('$location', ['url']);
        $provide.value('$location', $location);

        $route = jasmine.createSpyObj('$route', ['reload']);
        $provide.value('$route', $route);
    }));

    beforeEach(inject(function(_authenticationService_, _sharedDataService_, _$httpBackend_, _$q_, _$timeout_, _$rootScope_, _parseQuery_, _storageService_) {
        $timeout = _$timeout_;
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        $rootScope = _$rootScope_;
        storageService = _storageService_;
        parseQuery = _parseQuery_;
        authenticationService = _authenticationService_;
        sharedDataService = _sharedDataService_;

        spyOn(storageService, 'storeAuthToken').and.returnValue();
        spyOn(storageService, 'removeAuthToken').and.returnValue();
        spyOn(storageService, 'removeAllAuthTokens').and.callFake(function() {});
        spyOn(storageService, 'isInitialized').and.returnValue($q.when("someState"));

        spyOn(sharedDataService, 'get').and.callFake(function(key) {
            if (key === 'authenticationMap') {
                return $q.when({
                    "api1": "authEntryPoint1",
                    "api1more": "authEntryPoint2",
                    "api2": "authEntryPoint3"
                });
            } else if (key === 'credentialsMap') {
                return $q.when({
                    authEntryPoint1: {
                        client_id: "client_id_1",
                        client_secret: "client_secret_1"
                    },
                    authEntryPoint2: {
                        client_id: "client_id_2",
                        client_secret: "client_secret_2"
                    }
                });
            } else if (key === 'configuration') {
                return $q.when({
                    domain: 'thedomain'
                });
            }
        });

        systemEventService.publishAsync.and.returnValue($q.when());
    }));

    it('initializes and invokes gatewayProxy', function() {
        expect(authenticationService.gatewayId).toBe("authenticationService");
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(authenticationService);
    });

    it('isReAuthInProgress reads status set by setReAuthInProgress', function() {

        expect(authenticationService.isReAuthInProgress("someURL")).toBeResolvedWithData(false);
        authenticationService.setReAuthInProgress("someURL");
        expect(authenticationService.isReAuthInProgress("someURL")).toBeResolvedWithData(true);

    });

    it('WHEN an entry point is filtered using filterEntryPoints AND the entry point matches one in the default auth map THEN the auth entry points returned will include the matched entry point', function() {
        // WHEN
        var promise = authenticationService.filterEntryPoints('api3');

        // THEN
        expect(promise).toBeResolvedWithData([DEFAULT_AUTHENTICATION_ENTRY_POINT]);
    });

    it('filterEntryPoints only keeps the values of authenticationMap the regex keys of which match the resource', function() {

        authenticationService.filterEntryPoints("api1moreandmore").then(function(value) {
            expect(value).toEqualData(['authEntryPoint1', 'authEntryPoint2']);
        }, function() {
            expect().fail("failed to resolve to ['authEntryPoint1', 'authEntryPoint2']");
        });

        $rootScope.$digest();

        authenticationService.filterEntryPoints("api2/more").then(function(value) {
            expect(value).toEqualData(['authEntryPoint3']);
        }, function() {
            expect().fail("failed to resolve to ['authEntryPoint3']");
        });

        $rootScope.$digest();

        authenticationService.filterEntryPoints("notfound").then(function(value) {
            expect(value).toEqualData([]);
        }, function() {
            expect().fail("failed to resolve to []");
        });

        $rootScope.$digest();

    });

    it('isAuthEntryPoint returns true only if resource exactly matches at least one of the auth entry points or default auth entry point', function() {

        authenticationService.isAuthEntryPoint("api1moreandmore").then(function(value) {
            expect(value).toBe(false);
        }, function() {
            expect().fail("failed to resolve to false");
        });

        $rootScope.$digest();

        authenticationService.isAuthEntryPoint("authEntryPoint1").then(function(value) {
            expect(value).toBe(true);
        }, function() {
            expect().fail("failed to resolve to true");
        });

        $rootScope.$digest();

        authenticationService.isAuthEntryPoint("authEntryPoint1more").then(function(value) {
            expect(value).toBe(false);
        }, function() {
            expect().fail("failed to resolve to false");
        });

        $rootScope.$digest();


        authenticationService.isAuthEntryPoint(DEFAULT_AUTHENTICATION_ENTRY_POINT).then(function(value) {
            expect(value).toBe(true);
        }, function() {
            expect().fail("failed to resolve to false");
        });

        $rootScope.$digest();

    });

    it('WHEN the entry point matches one in the default, default auth entry point is returned along with default client id', function() {
        // WHEN
        expect(authenticationService._findAuthURIAndClientCredentials('api3')).toBeResolvedWithData({
            authURI: DEFAULT_AUTHENTICATION_ENTRY_POINT,
            clientCredentials: {
                client_id: 'smartedit'
            }
        });

    });

    it('WHEN the entry point matches one in the auth map, corresponding entry point is returned along with relevant credentials', function() {
        // WHEN
        expect(authenticationService._findAuthURIAndClientCredentials('api1more')).toBeResolvedWithData({
            authURI: 'authEntryPoint1',
            clientCredentials: {
                client_id: 'client_id_1',
                client_secret: 'client_secret_1'
            }
        });

    });

    it('authenticate will launch modalService and remove authInprogress flag', function() {

        modalService.open.and.returnValue($q.when("something"));
        languageService.isInitialized.and.returnValue($q.when());
        sessionService.hasUserChanged.and.returnValue($q.resolve(false));

        authenticationService.setReAuthInProgress("authEntryPoint1");
        authenticationService.authenticate("api1/more").then(function() {}, function() {
            expect().fail("failed to resolve to false");
        });

        $rootScope.$digest();

        expect(modalService.open).toHaveBeenCalledWith({
            cssClasses: "se-login-modal",
            templateUrl: 'loginDialog.html',
            controller: jasmine.any(Array)
        });
        expect(languageService.isInitialized).toHaveBeenCalled();
        expect(authenticationService.isReAuthInProgress("authEntryPoint1")).toBeResolvedWithData(false);

    });

    it('should return false when the access_token is not found in storage', function() {

        var entryPoints = ['entryPoint1'];
        spyOn(authenticationService, 'filterEntryPoints').and.returnValue($q.when(entryPoints));
        spyOn(storageService, 'getAuthToken').and.returnValue($q.when(null));

        expect(authenticationService.isAuthenticated('url')).toBeResolvedWithData(false);

        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
        expect(storageService.getAuthToken).toHaveBeenCalledWith('entryPoint1');
    });

    it('should return true when the access_token is found in the storage', function() {

        var entryPoints = ['entryPoint1'];
        spyOn(authenticationService, 'filterEntryPoints').and.returnValue($q.when(entryPoints));

        var authToken = {
            access_token: 'access-token1',
            token_type: 'bearer'
        };
        spyOn(storageService, 'getAuthToken').and.returnValue($q.when(authToken));

        expect(authenticationService.isAuthenticated('url')).toBeResolvedWithData(true);

        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
        expect(storageService.getAuthToken).toHaveBeenCalledWith('entryPoint1');
    });

    it('should return false when the entry point is not found in the authentication', function() {

        spyOn(authenticationService, 'filterEntryPoints').and.returnValue($q.when(null));
        spyOn(storageService, 'getAuthToken').and.returnValue($q.when(null));

        expect(authenticationService.isAuthenticated('url')).toBeResolvedWithData(false);

        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
        expect(storageService.getAuthToken).toHaveBeenCalledWith(null);
    });


    describe('controller of authenticationService with non default endpoint', function() {

        beforeEach(function() {
            modalService.open.and.returnValue($q.when("something"));
            languageService.isInitialized.and.returnValue($q.when());
            sessionService.hasUserChanged.and.returnValue($q.when(false));

            authenticationService.authenticate("api1/more").then(function() {}, function() {
                expect().fail("failed to resolve to false");
            });

            $rootScope.$digest();

            var controller = modalService.open.calls.argsFor(0)[0].controller[1];
            expect(controller).toBeDefined();

            modalManager = jasmine.createSpyObj('modalManager', ['setShowHeaderDismiss', 'close']);

            controllerContext = {};
            controller.call(controllerContext, modalManager);

            systemEventService.publishAsync.and.callFake(function() {
                return $q.when();
            });
        });

        it('is set with the expected properties and functions', function() {

            expect(controllerContext.authURI).toBe("authEntryPoint1");
            expect(controllerContext.auth).toEqual({
                username: '',
                password: ''
            });

            expect(modalManager.setShowHeaderDismiss).toHaveBeenCalledWith(false);
            expect(controllerContext.initialized).toBe("someState");
            expect(storageService.isInitialized).toHaveBeenCalled();
        });

        it('submit will be rejected is form is invalid', function() {

            controllerContext.auth = {
                username: 'someusername',
                password: 'somepassword'
            };

            var loginDialogForm = {
                $valid: false
            };

            controllerContext.submit(loginDialogForm).then(function() {
                expect().fail("submit should have rejected");
            });

            expect(sessionService.setCurrentUsername).not.toHaveBeenCalled();
            expect(storageService.storeAuthToken).not.toHaveBeenCalled();
            expect(storageService.removeAuthToken).toHaveBeenCalled();
        });

        it('submit will prepare a payload with optional credentials to auth entry point and resolves and store AuthToken', function() {

            controllerContext.auth = {
                username: 'someusername',
                password: 'somepassword'
            };
            var oauthToken = {
                access_token: 'access-token1',
                token_type: 'bearer'
            };
            $httpBackend.whenPOST('authEntryPoint1').respond(function(method, url, data) {
                var expectedPayload = {
                    client_id: 'client_id_1',
                    client_secret: 'client_secret_1',
                    username: 'someusername',
                    password: 'somepassword',
                    grant_type: 'password'
                };
                if (angular.equals(expectedPayload, parseQuery(data))) {
                    return [200, oauthToken];
                } else {
                    return [401];
                }
            });

            var loginDialogForm = {
                $valid: true
            };

            controllerContext.submit(loginDialogForm).then(function() {}, function() {
                expect().fail("submit should have resolved");
            });

            expect(sessionService.setCurrentUsername).not.toHaveBeenCalled();
            expect(storageService.storeAuthToken).not.toHaveBeenCalled();
            $httpBackend.flush();
            expect(sessionService.setCurrentUsername).not.toHaveBeenCalled();
            expect(storageService.storeAuthToken).toHaveBeenCalledWith("authEntryPoint1", oauthToken);
            expect(storageService.removeAuthToken).toHaveBeenCalled();
            expect(modalManager.close).toHaveBeenCalledWith({
                userHasChanged: false
            });
        });

        it('submit will prepare a payload with optional credentials to auth entry point and reject and remove AuthToken', function() {

            controllerContext.auth = {
                username: 'someusername',
                password: 'somepassword'
            };

            $httpBackend.whenPOST('authEntryPoint1').respond(function(method, url, data) {
                var expectedPayload = {
                    client_id: 'client_id_1',
                    client_secret: 'client_secret_1',
                    username: 'someusername',
                    password: 'somepassword',
                    grant_type: 'password'
                };
                if (angular.equals(expectedPayload, parseQuery(data))) {
                    return [401, {
                        error_description: 'Required fields are missing'
                    }];
                }
            });

            var loginDialogForm = {
                $valid: true
            };

            controllerContext.submit(loginDialogForm).then(function() {
                expect().fail("submit should have rejected");
            }, function() {});

            expect(storageService.removeAuthToken).toHaveBeenCalled();
            $httpBackend.flush();
            expect(storageService.removeAuthToken).toHaveBeenCalledWith("authEntryPoint1");
            expect(sessionService.setCurrentUsername).not.toHaveBeenCalled();
            expect(storageService.storeAuthToken).not.toHaveBeenCalled();
            expect(loginDialogForm.errorMessage).toBe('Required fields are missing');
            expect(modalManager.close).not.toHaveBeenCalledWith({
                userHasChanged: false
            });
        });

        it('submit will be rejected and oauth wont respond the request then the error message should use default', function() {

            controllerContext.auth = {
                username: 'someusername',
                password: 'somepassword'
            };

            $httpBackend.whenPOST('authEntryPoint1').respond(function(method, url, data) {
                var expectedPayload = {
                    client_id: 'client_id_1',
                    client_secret: 'client_secret_1',
                    username: 'someusername',
                    password: 'somepassword',
                    grant_type: 'password'
                };
                if (angular.equals(expectedPayload, parseQuery(data))) {
                    return [401];
                }
            });

            var loginDialogForm = {
                $valid: true
            };

            controllerContext.submit(loginDialogForm).then(function() {
                expect().fail("submit should have rejected");
            }, function() {});

            $httpBackend.flush();
            expect(loginDialogForm.errorMessage).toBe('se.logindialogform.oauth.error.default');
        });

        it('invalid form will be rejected then the error message should be populated with invalid form', function() {

            var loginDialogForm = {
                $valid: false
            };

            controllerContext.submit(loginDialogForm).then(function() {
                expect().fail("submit should have rejected");
            }, function() {});

            expect(loginDialogForm.errorMessage).toBe('se.logindialogform.username.and.password.required');
        });

        it('will be rejected with sanitized error message from server error response', function() {

            // GIVEN
            var MESSAGE = "{{0[a='constructor'][a]('alert(\"XSS\")')()}}password";
            var SANITIZED_MESSAGE = "{{0[a='constructor'][a]\\('alert\\(\"XSS\"\\)'\\)\\(\\)}}password";
            var loginDialogForm = {
                $valid: true
            };
            $httpBackend.whenPOST('authEntryPoint1').respond(function() {
                return [400, {
                    error_description: MESSAGE
                }];
            });

            // WHEN
            controllerContext.submit(loginDialogForm);
            $httpBackend.flush();

            //THEN
            expect(loginDialogForm.errorMessage).toBe(SANITIZED_MESSAGE);
        });

        it('logout will remove auth tokens from cookie and reload current page if current page is landing page', function() {
            $location.url.and.callFake(function(arg) {
                if (!arg) {
                    return "/";
                }
            });
            var result = authenticationService.logout();
            $rootScope.$apply();

            result.then(function() {
                expect(storageService.removeAllAuthTokens).toHaveBeenCalled();
                expect($location.url).toHaveBeenCalledWith();
                expect($route.reload).toHaveBeenCalled();
            });
        });

        it('logout will remove auth tokens from cookie and reload current page if current page is empty', function() {

            $location.url.and.callFake(function(arg) {
                if (!arg) {
                    return "";
                }
            });
            var result = authenticationService.logout();
            $rootScope.$digest();

            result.then(function() {
                expect(storageService.removeAllAuthTokens).toHaveBeenCalled();
                expect($location.url).toHaveBeenCalledWith();
                expect($route.reload).toHaveBeenCalled();
            });
        });

        it('logout will remove auth tokens from cookie and redirect to landing page if current page is not landing page', function() {

            $location.url.and.callFake(function(arg) {
                if (!arg) {
                    return "/somepage";
                }
            });
            var result = authenticationService.logout();
            $rootScope.$digest();

            result.then(function() {
                expect(storageService.removeAllAuthTokens).toHaveBeenCalled();
                expect($location.url.calls.count()).toBe(2);
                expect($location.url.calls.argsFor(0)).toEqual([]);
                expect($location.url.calls.argsFor(1)).toEqual(["/"]);
            });
        });

        it('WHEN logout is called THEN a LOGOUT event is raised', function() {
            // Arrange

            // Act
            var result = authenticationService.logout();
            $rootScope.$digest();

            // Assert
            result.then(function() {
                expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENTS.LOGOUT);
            });
        });

    });

    describe('controller of authenticationService with default endpoint', function() {

        var prepareEnv = function(config) {
            modalService.open.and.returnValue($q.when({
                userHasChanged: config.userHasChanged
            }));
            languageService.isInitialized.and.returnValue($q.when());
            authenticationService.authenticate("api3/more");
            $rootScope.$digest();

            var controller = modalService.open.calls.argsFor(0)[0].controller[1];
            expect(controller).toBeDefined();

            modalManager = jasmine.createSpyObj('modalManager', ['setShowHeaderDismiss', 'close']);

            controllerContext = {};
            controller.call(controllerContext, modalManager);

            systemEventService.publishAsync.and.callFake(function() {
                return $q.when();
            });
        };

        describe('the previous user is initialized AND previous user is not the same as a new one', function() {
            beforeEach(function() {
                sessionService.hasUserChanged.and.returnValue($q.when(true));
                prepareEnv({
                    userHasChanged: true
                });
            });

            it('GIVEN payload with credentials WHEN submit is called THEN principal identifier should be set AND the page should be reloaded AND close function should contain {userHasChanged: true}', function() {

                // GIVEN
                controllerContext.auth = {
                    username: 'someusername',
                    password: 'somepassword'
                };
                var oauthToken = {
                    access_token: 'access-token1',
                    token_type: 'bearer'
                };
                $httpBackend.whenPOST(DEFAULT_AUTHENTICATION_ENTRY_POINT).respond(function(method, url, data) {
                    var expectedPayload = {
                        client_id: "smartedit",
                        grant_type: "password",
                        password: "somepassword",
                        username: "someusername"
                    };
                    if (angular.equals(expectedPayload, parseQuery(data))) {
                        return [200, oauthToken];
                    } else {
                        return [401];
                    }
                });

                // WHEN
                controllerContext.submit({
                    $valid: true
                });
                $httpBackend.flush();

                // THEN
                expect(modalManager.close).toHaveBeenCalledWith({
                    userHasChanged: true
                });
                expect(sessionService.setCurrentUsername).toHaveBeenCalled();
                expect($route.reload).toHaveBeenCalled();
            });
        });

        describe('the previous user is initialized AND previous user is the same as a new one', function() {
            beforeEach(function() {
                sessionService.hasUserChanged.and.returnValue($q.when(false));
                prepareEnv({
                    userHasChanged: false
                });
            });

            it('GIVEN payload with credentials WHEN submit is called THEN principal identifier should be set AND the page should not be reloaded AND close function should contain {userHasChanged: false}', function() {

                // GIVEN
                controllerContext.auth = {
                    username: 'previousUser',
                    password: 'somepassword'
                };
                var oauthToken = {
                    access_token: 'access-token1',
                    token_type: 'bearer'
                };
                $httpBackend.whenPOST(DEFAULT_AUTHENTICATION_ENTRY_POINT).respond(function(method, url, data) {
                    var expectedPayload = {
                        client_id: "smartedit",
                        grant_type: "password",
                        password: "somepassword",
                        username: "previousUser"
                    };
                    if (angular.equals(expectedPayload, parseQuery(data))) {
                        return [200, oauthToken];
                    } else {
                        return [401];
                    }
                });

                // WHEN
                controllerContext.submit({
                    $valid: true
                });
                $httpBackend.flush();

                // THEN
                expect(modalManager.close).toHaveBeenCalledWith({
                    userHasChanged: false
                });
                expect(sessionService.setCurrentUsername).toHaveBeenCalled();
                expect($route.reload).not.toHaveBeenCalled();
            });
        });

        describe('the user is logged in for the first time (previous user is undefined)', function() {
            beforeEach(function() {
                sessionService.hasUserChanged.and.returnValue($q.when(false));
                prepareEnv({
                    userHasChanged: false
                });
            });

            it('GIVEN payload with credentials WHEN submit is called THEN principal identifier should be set AND the page should not be reloaded AND close function should contain {userHasChanged: false} ', function() {
                // GIVEN
                controllerContext.auth = {
                    username: 'someusername',
                    password: 'somepassword'
                };
                var oauthToken = {
                    access_token: 'access-token1',
                    token_type: 'bearer'
                };
                $httpBackend.whenPOST(DEFAULT_AUTHENTICATION_ENTRY_POINT).respond(function(method, url, data) {
                    var expectedPayload = {
                        client_id: "smartedit",
                        grant_type: "password",
                        password: "somepassword",
                        username: "someusername"
                    };
                    if (angular.equals(expectedPayload, parseQuery(data))) {
                        return [200, oauthToken];
                    } else {
                        return [401];
                    }
                });

                // WHEN
                controllerContext.submit({
                    $valid: true
                });
                $httpBackend.flush();

                // THEN
                expect(modalManager.close).toHaveBeenCalledWith({
                    userHasChanged: false
                });
                expect(sessionService.setCurrentUsername).toHaveBeenCalled();
                expect($route.reload).not.toHaveBeenCalled();
            });
        });
    });
});
