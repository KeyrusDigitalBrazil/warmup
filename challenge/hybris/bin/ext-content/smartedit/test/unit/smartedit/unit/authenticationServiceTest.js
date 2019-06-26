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
describe('inner AuthenticationService', function() {

    var gatewayProxy, authenticationService, AuthenticationServiceInterface;

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {
        var gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', gatewayFactory);
        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(angular.mock.module("authenticationModule"));

    beforeEach(inject(function(_AuthenticationServiceInterface_, _authenticationService_) {
        AuthenticationServiceInterface = _AuthenticationServiceInterface_;
        authenticationService = _authenticationService_;
    }));

    it('extends AuthenticationServiceInterface', function() {
        expect(authenticationService instanceof AuthenticationServiceInterface).toBe(true);
    });
    it('initializes and invokes gatewayProxy', function() {
        expect(authenticationService.gatewayId).toBe("authenticationService");
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(authenticationService);
    });

    it('leaves all interface functions unimplemented', function() {
        expect(authenticationService.authenticate).toBeEmptyFunction();
        expect(authenticationService.logout).toBeEmptyFunction();
        expect(authenticationService.isReAuthInProgress).toBeEmptyFunction();
        expect(authenticationService.setReAuthInProgress).toBeEmptyFunction();
        expect(authenticationService.filterEntryPoints).toBeEmptyFunction();
        expect(authenticationService.isAuthEntryPoint).toBeEmptyFunction();
        expect(authenticationService.isAuthenticated).toBeEmptyFunction();
    });

});
