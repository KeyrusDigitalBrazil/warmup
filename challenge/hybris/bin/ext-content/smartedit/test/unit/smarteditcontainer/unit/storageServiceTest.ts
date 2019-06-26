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
import 'jasmine';
import * as lo from 'lodash';
import {coreAnnotationsHelper, promiseHelper, IExtensiblePromise} from 'testhelpers';
import {annotationService, GatewayProxied, IAuthToken} from 'smarteditcommons';
import {StorageService} from 'smarteditcontainer/services';

describe('outer storage service', () => {

	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;
	const $cookies: jasmine.SpyObj<angular.cookies.ICookiesService> = jasmine.createSpyObj('$cookies', ['get', 'put', 'remove']);
	const $q: jasmine.SpyObj<angular.IQService> = promiseHelper.$q();
	const sessionService: any = jasmine.createSpyObj('sessionService', ['getCurrentUsername', 'resetCurrentUserData', 'setCurrentUsername']);
	const $log: jasmine.SpyObj<angular.ILogService> = jasmine.createSpyObj('$log', ['error']);
	let $injectorMock: jasmine.SpyObj<angular.auto.IInjectorService>;

	const $window = {
		location: {
			protocol: jasmine.createSpyObj("$window.location.protocol", ["indexOf"])
		}
	};

	let storageService: StorageService;

	beforeEach(() => {
		$injectorMock = jasmine.createSpyObj('$injector', ['has', 'get']);
		$window.location.protocol.indexOf.and.returnValue(-1);
		coreAnnotationsHelper.init();

		storageService = new StorageService(lodash, $q, $injectorMock, $cookies, $window as ng.IWindowService, $log);

		$injectorMock.get.and.returnValue(sessionService);
	});

	it('checks GatewayProxied', () => {
		const decoratorObj = annotationService.getClassAnnotation(StorageService, GatewayProxied);
		expect(decoratorObj).toEqual(['isInitialized', 'storeAuthToken', 'getAuthToken', 'removeAuthToken', 'removeAllAuthTokens', 'storePrincipalIdentifier', 'getPrincipalIdentifier', 'removePrincipalIdentifier', 'getValueFromCookie']);
	});

	it('isInitialized', () => {
		// GIVEN
		const authTokens = {
			'entryPoint1': {
				access_token: 'access_token1',
				token_type: 'bearer'
			},
			'entryPoint2': {
				access_token: 'access_token2',
				token_type: 'bearer'
			},
			'principal-uid': 'someUserName'
		};
		$cookies.get.and.returnValue(btoa(JSON.stringify(authTokens)));

		// WHEN
		const promise = storageService.isInitialized() as IExtensiblePromise<boolean>;

		// THEN
		expect(promise.value).toBe(true);
	});

	it('removeAllAuthTokens will remove from smartedit-sessions cookie', function() {
		// GIVEN
		const authTokens = {
			entryPoint1: {
				access_token: 'access_token1',
				token_type: 'bearer'
			},
			entryPoint2: {
				access_token: 'access_token2',
				token_type: 'bearer'
			},
			custom_properties: {}
		};

		$cookies.get.and.returnValue(btoa(JSON.stringify(authTokens)));

		// WHEN
		storageService.removeAllAuthTokens();

		// THEN
		expect($cookies.put).toHaveBeenCalledWith(
			'smartedit-sessions', btoa(JSON.stringify({
				custom_properties: {}
			})), {secure: false}
		);
	});

	it('removeAuthToken for entryPoint1 will remove the entry from smartedit-sessions cookie', function() {

		const authTokens = {
			entryPoint1: {
				access_token: 'access_token1',
				token_type: 'bearer'
			},
			entryPoint2: {
				access_token: 'access_token2',
				token_type: 'bearer'
			}
		};
		$cookies.get.and.returnValue(btoa(JSON.stringify(authTokens)));

		storageService.removeAuthToken("entryPoint1");

		expect($cookies.put).toHaveBeenCalledWith("smartedit-sessions", btoa(JSON.stringify({
			entryPoint2: {
				access_token: 'access_token2',
				token_type: 'bearer'
			}
		})), {secure: false});
	});

	it('removeAuthToken for entryPoint1 will remove the entire smartedit-sessions cookie', function() {

		const authTokens = {
			entryPoint1: {
				access_token: 'access_token1',
				token_type: 'bearer'
			}
		};
		$cookies.get.and.returnValue(btoa(JSON.stringify(authTokens)));

		storageService.removeAuthToken("entryPoint1");

		expect($cookies.put).toHaveBeenCalledWith("smartedit-sessions", btoa(JSON.stringify({})), {secure: false});
	});


	it('getAuthToken will get the auth token specific to the given entry point from smartedit-sessions cookie', function() {

		const authTokens = {
			entryPoint1: {
				access_token: 'access_token1',
				token_type: 'bearer'
			},
			entryPoint2: {
				access_token: 'access_token2',
				token_type: 'bearer'
			}
		};
		$cookies.get.and.returnValue(btoa(JSON.stringify(authTokens)));

		const promise = storageService.getAuthToken("entryPoint2") as IExtensiblePromise<IAuthToken>;

		expect(promise.value).toEqual({
			access_token: 'access_token2',
			token_type: 'bearer'
		} as IAuthToken);

		expect($cookies.get).toHaveBeenCalledWith("smartedit-sessions");
	});

	it('storeAuthToken will store the given auth token in a new map with the entryPoint as the key in smartedit-sessions cookie', function() {

		$cookies.get.and.returnValue(null);

		storageService.storeAuthToken("entryPoint1", {
			access_token: 'access_token1',
			token_type: 'bearer'
		} as IAuthToken);

		expect($cookies.put).toHaveBeenCalledWith("smartedit-sessions", btoa(JSON.stringify({
			entryPoint1: {
				access_token: 'access_token1',
				token_type: 'bearer'
			}
		})), {secure: false});
	});

	it('storeAuthToken will store the given auth token in existing map with the entryPoint as the key in pre-existing smartedit-sessions cookie', function() {

		const authTokens = {
			entryPoint2: {
				access_token: 'access_token2',
				token_type: 'bearer'
			}
		};
		$cookies.get.and.returnValue(btoa(JSON.stringify(authTokens)));

		storageService.storeAuthToken("entryPoint1", {
			access_token: 'access_token1',
			token_type: 'bearer'
		} as IAuthToken);

		expect($cookies.put).toHaveBeenCalledWith("smartedit-sessions", btoa(JSON.stringify({
			entryPoint2: {
				access_token: 'access_token2',
				token_type: 'bearer'
			},
			entryPoint1: {
				access_token: 'access_token1',
				token_type: 'bearer'
			}
		})), {secure: false});
	});

	it('getPrincipalIdentifier() will display a log warning that the method is deprecated', function() {
		// When
		storageService.getPrincipalIdentifier();
		// Assert
		expect($injectorMock.get).toHaveBeenCalledWith('sessionService');
		expect(sessionService.getCurrentUsername).toHaveBeenCalled();
	});

	it('IF no cookie is stored WHEN getValueFromCookie is called THEN null is returned', function() {
		// Arrange
		$cookies.get.and.returnValue(null);

		// Act
		const promise = storageService.getValueFromCookie('someCookie', true) as IExtensiblePromise<any>;

		// Assert
		expect($cookies.get).toHaveBeenCalledWith('someCookie');
		expect(promise.value).toBe(null);
	});

	it('IF cookie value is not JSON parsable WHEN getValueFromCookie is called THEN null is returned', function() {
		// Arrange
		$cookies.get.and.returnValue("{");

		// Act
		const promise = storageService.getValueFromCookie('someCookie', true) as IExtensiblePromise<any>;

		// Assert
		expect($cookies.get).toHaveBeenCalledWith('someCookie');
		expect(promise.value).toBe(null);
	});


	it('IF a cookie is stored and its value is not encoded WHEN getValueFromCookie is called THEN the value is returned', function() {
		// Arrange
		const rawValue = "se.none";
		$cookies.get.and.returnValue(JSON.stringify(rawValue));

		// Act
		const promise = storageService.getValueFromCookie('someCookie', false) as IExtensiblePromise<any>;

		// Assert
		expect($cookies.get).toHaveBeenCalledWith('someCookie');
		expect(promise.value).toBe(rawValue);
	});

	it('IF no cookie is stored and its value is encoded WHEN getValueFromCookie is called THEN the un-encoded value is returned', function() {
		// Arrange
		const rawValue = "se.none";
		const encodedValue = "InNlLm5vbmUi";
		$cookies.get.and.returnValue(encodedValue);

		// Act
		const promise = storageService.getValueFromCookie('someCookie', true) as IExtensiblePromise<any>;

		// Assert
		expect($cookies.get).toHaveBeenCalledWith('someCookie');
		expect(promise.value).toBe(rawValue);
	});

	it('WHEN putValueInCookie is called and the encode flag is not set THEN the un-encoded value is stored', function() {
		// Arrange
		const rawValue = {
			key: "se.none"
		};
		$cookies.put.and.returnValue(null);

		// Act
		storageService.putValueInCookie('someCookie', rawValue, false);

		// Assert
		expect($cookies.put).toHaveBeenCalledWith('someCookie', JSON.stringify(rawValue), {secure: false});
	});

	it('WHEN putValueInCookie is called and the encode flag is set THEN the encoded value is stored', function() {
		// Arrange
		const rawValue = '"se.none"';
		const encodedValue = "Ilwic2Uubm9uZVwiIg==";
		$cookies.put.and.returnValue(null);

		// Act
		storageService.putValueInCookie('someCookie', rawValue, true);

		// Assert
		expect($cookies.put).toHaveBeenCalledWith('someCookie', encodedValue, {secure: false});
	});
});
