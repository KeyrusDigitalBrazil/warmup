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
describe('unauthorized error interceptor', function() {
    var $httpBackend;
    var $q;
    var $rootScope;
    var unauthorizedErrorInterceptor;
    var authenticationService;
    var who_am_i_url = "/authorizationserver/oauth/whoami";

    beforeEach(function() {
        angular.module('authenticationModule');
    });

    beforeEach(angular.mock.module('unauthorizedErrorInterceptorModule', function($provide) {
        authenticationService = jasmine.createSpyObj("authenticationService", ["isReAuthInProgress", "setReAuthInProgress", "isAuthEntryPoint", "filterEntryPoints", "authenticate"]);
        authenticationService.setReAuthInProgress.and.callFake(function() {
            return $q.when();
        });
        authenticationService.isAuthEntryPoint.and.returnValue(true);
        $provide.value("authenticationService", authenticationService);
    }));

    beforeEach(inject(function(_unauthorizedErrorInterceptor_) {
        unauthorizedErrorInterceptor = _unauthorizedErrorInterceptor_;
    }));

    beforeEach(inject(function(_$q_, _$httpBackend_, _$rootScope_) {
        $q = _$q_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
    }));

    it('should match predicate for a xhr request with a HTTP Error 401', function() {
        // GIVEN
        var matchMockResponse = {
            status: 401
        };

        // WHEN
        var matchPredicate = unauthorizedErrorInterceptor.predicate(matchMockResponse);

        // THEN
        expect(matchPredicate).toBe(true);
    });

    it('should not match predicate for a 401 returned by the "Who am I" service', function() {
        // GIVEN
        var matchMockResponse = {
            status: 401,
            config: {
                url: who_am_i_url
            }
        };

        // WHEN
        var matchPredicate = unauthorizedErrorInterceptor.predicate(matchMockResponse);

        // THEN
        expect(matchPredicate).toBe(false);
    });


    it('should not match predicate for a xhr request with a HTTP Error 400 or 404', function() {
        var predicate;
        // GIVEN
        [400, 404].forEach(function(status) {
            // WHEN
            predicate = unauthorizedErrorInterceptor.predicate({
                status: status
            });

            // THEN
            expect(predicate).toBe(false);
        });
    });

    it('should handle a 401 unauthorized request and reattempt the same request after a successfull user authentication', function() {
        var mockResponse = {
            status: 401,
            config: {
                method: 'GET',
                url: '/any_url'
            },
            data: {}
        };
        //final response once authenticated
        var newResponse = 'anyReponse';
        var authEntryPoint = 'authEntryPoint';

        authenticationService.filterEntryPoints.and.returnValue($q.when([authEntryPoint]));
        authenticationService.isAuthEntryPoint.and.returnValue($q.when(false));
        authenticationService.isReAuthInProgress.and.returnValue($q.when(false));
        authenticationService.authenticate.and.returnValue($q.when());

        $httpBackend.whenGET(mockResponse.config.url).respond(newResponse);

        unauthorizedErrorInterceptor.responseError(mockResponse).then(function(success) {
            expect(success.data).toBe(newResponse);
        }, function(error) {
            expect(error).fail('the final request should have been successful');
        });
        $httpBackend.flush();

        expect(authenticationService.isAuthEntryPoint).toHaveBeenCalledWith(mockResponse.config.url);
        expect(authenticationService.isReAuthInProgress).toHaveBeenCalledWith(authEntryPoint);
        expect(authenticationService.setReAuthInProgress).toHaveBeenCalledWith(authEntryPoint);
        expect(authenticationService.authenticate).toHaveBeenCalledWith(mockResponse.config.url);
    });

    it('if more than one response error is 401 and not auth URL then authentication is invoked ONLY when same authEntryPoint and all promises of a reattempt are sent back', function() {
        var mockResponse1 = {
            status: 401,
            config: {
                method: 'GET',
                url: 'request1'
            },
            data: {}
        };
        var mockResponse2 = {
            status: 401,
            config: {
                method: 'POST',
                url: 'request2'
            },
            data: {}
        };
        //final response once authenticated
        var newResponse1 = 'anyReponse';
        var newResponse2 = 'anyReponse2';
        var authEntryPoint = 'authEntryPoint';

        var authDeferred = $q.defer();

        authenticationService.filterEntryPoints.and.returnValue($q.when([authEntryPoint]));
        authenticationService.isAuthEntryPoint.and.returnValue($q.when(false));
        authenticationService.isReAuthInProgress.and.returnValue($q.when(false));
        authenticationService.authenticate.and.returnValue(authDeferred.promise);

        $httpBackend.expectGET(mockResponse1.config.url).respond(newResponse1);
        $httpBackend.expectPOST(mockResponse2.config.url).respond(newResponse2);

        unauthorizedErrorInterceptor.responseError(mockResponse1).then(function(success) {
            expect(success.data).toBe(newResponse1);
        }, function(error) {
            expect(error).fail('the final request should have been successful');
        });

        $rootScope.$digest();

        authenticationService.isReAuthInProgress.and.callFake(function(entryPoint) {
            if (entryPoint === authEntryPoint) {
                return $q.when(true);
            } else {
                return $q.when(false);
            }
        });

        unauthorizedErrorInterceptor.responseError(mockResponse2).then(function(success) {
            expect(success.data).toBe(newResponse2);
        }, function(error) {
            expect(error).fail('the final request should have been successful');
        });

        $rootScope.$digest();

        expect(authenticationService.isReAuthInProgress.calls.count()).toBe(2);
        expect(authenticationService.setReAuthInProgress.calls.count()).toBe(1);
        expect(authenticationService.setReAuthInProgress).toHaveBeenCalledWith(authEntryPoint);
        expect(authenticationService.authenticate.calls.count()).toBe(1);

        authDeferred.resolve();
        $httpBackend.flush();
    });

    it('if more than one response error is 401 and not auth URL then authentication is invoked once per authEntryPoint and all promises of a reattempt are sent back', function() {
        var response1 = {
            status: 401,
            config: {
                method: 'GET',
                url: 'request1'
            },
            data: {}
        };
        var response2 = {
            status: 401,
            config: {
                method: 'POST',
                url: 'request2'
            },
            data: {}
        };
        //final response once authenticated
        var newResponse1 = "anyReponse1";
        var newResponse2 = "anyReponse2";

        var authDeferred = $q.defer();

        authenticationService.filterEntryPoints.and.callFake(function(url) {
            if (url === 'request1') {
                return $q.when(['authEntryPoint1']);
            } else if (url === 'request2') {
                return $q.when(['authEntryPoint2']);
            }
        });
        authenticationService.isAuthEntryPoint.and.returnValue($q.when(false));
        authenticationService.isReAuthInProgress.and.returnValue($q.when(false));
        authenticationService.authenticate.and.returnValue(authDeferred.promise);

        $httpBackend.expectGET('request1').respond(newResponse1);
        $httpBackend.expectPOST('request2').respond(newResponse2);

        unauthorizedErrorInterceptor.responseError(response1).then(function(success) {
            expect(success.data).toBe(newResponse1);
        }, function(error) {
            expect(error).fail("the final request should have been successful");
        });

        $rootScope.$digest();

        authenticationService.isReAuthInProgress.and.callFake(function(authEntryPoint) {
            if (authEntryPoint === 'authEntryPoint1') {
                return $q.when(true);
            } else if (authEntryPoint === 'authEntryPoint2') {
                return $q.when(false);
            }
        });

        unauthorizedErrorInterceptor.responseError(response2).then(function(success) {
            expect(success.data).toBe(newResponse2);
        }, function(error) {
            expect(error).fail("the final request should have been successful");
        });

        $rootScope.$digest();

        expect(authenticationService.isReAuthInProgress.calls.count()).toBe(2);
        expect(authenticationService.setReAuthInProgress.calls.count()).toBe(2);
        expect(authenticationService.setReAuthInProgress).toHaveBeenCalledWith('authEntryPoint1');
        expect(authenticationService.setReAuthInProgress).toHaveBeenCalledWith('authEntryPoint2');
        expect(authenticationService.authenticate.calls.count()).toBe(2);

        authDeferred.resolve();
        $httpBackend.flush();
    });
});
