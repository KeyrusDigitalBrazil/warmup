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
describe('httpAuthInterceptor', function() {
    var $q, $rootScope, $httpProvider, httpAuthInterceptor, authenticationService, storageService;

    beforeEach(angular.mock.module('httpAuthInterceptorModule', function($provide, _$httpProvider_) {
        $httpProvider = _$httpProvider_;
        authenticationService = jasmine.createSpyObj("authenticationService", ["filterEntryPoints"]);
        $provide.value("authenticationService", authenticationService);

        storageService = jasmine.createSpyObj("storageService", ["getAuthToken"]);
        $provide.value("storageService", storageService);
    }));

    beforeEach(inject(function(_$q_, _$rootScope_, _httpAuthInterceptor_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        httpAuthInterceptor = _httpAuthInterceptor_;
        storageService.getAuthToken.and.returnValue($q.when());
    }));

    it('$httpProvider will be loaded with only one interceptor and that will be the httpAuthInterceptor', function() {
        expect($httpProvider.interceptors).toContain('httpAuthInterceptor');
    });

    it('if url is html, config is returned, not a promise and neiher authenticationService nor storageService are ever invoked', function() {
        var config = {
            url: 'somepath/somefile.html',
            headers: {
                'key': 'value'
            }
        };

        expect(httpAuthInterceptor.request(config)).toBe(config);
        expect(httpAuthInterceptor.request(config)).toEqual(config);

        $rootScope.$digest();

        expect(config.headers).toEqual({
            key: 'value'
        });
        expect(authenticationService.filterEntryPoints).not.toHaveBeenCalled();
        expect(storageService.getAuthToken).not.toHaveBeenCalled();
    });

    it('if access_token present found, it will be added to outgoing request', function() {
        var config = {
            url: 'aurl',
            headers: {}
        };
        var authToken = {
            access_token: 'access-token1',
            token_type: 'bearer'
        };

        var entryPoints = ['entryPoint1'];
        authenticationService.filterEntryPoints.and.returnValue($q.when(entryPoints));

        storageService.getAuthToken.and.returnValue($q.when(authToken));

        expect(httpAuthInterceptor.request(config)).toBeResolvedWithData(config);
        expect(config.headers.Authorization).toBeDefined();
        expect(config.headers.Authorization).toBe(["bearer", authToken.access_token].join(" "));
        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith(config.url);
        expect(storageService.getAuthToken).toHaveBeenCalledWith('entryPoint1');
    });

    it('if access_token not found in storage, no authorization header is added to outgoing request', function() {
        var config = {
            url: 'aurl',
            headers: {}
        };

        var entryPoints = ['entryPoint1'];
        authenticationService.filterEntryPoints.and.returnValue($q.when(entryPoints));

        storageService.getAuthToken.and.returnValue($q.when(null));

        httpAuthInterceptor.request(config).then(function(returnedConfig) {
            expect(returnedConfig).toBe(config);
        }, function() {
            expect().fail();
        });

        $rootScope.$digest();

        expect(config.headers.Authorization).not.toBeDefined();
        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('aurl');
        expect(storageService.getAuthToken).toHaveBeenCalledWith('entryPoint1');
    });

    it('if API pattern not recognised, no authorization header is added to outgoing request', function() {
        var config = {
            url: 'aurl',
            headers: {}
        };

        authenticationService.filterEntryPoints.and.returnValue($q.when([]));

        httpAuthInterceptor.request(config).then(function(returnedConfig) {
            expect(returnedConfig).toBe(config);
        }, function() {
            expect().fail();
        });

        $rootScope.$digest();

        expect(config.headers.Authorization).not.toBeDefined();
        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('aurl');
        expect(storageService.getAuthToken).not.toHaveBeenCalled();
    });

});
