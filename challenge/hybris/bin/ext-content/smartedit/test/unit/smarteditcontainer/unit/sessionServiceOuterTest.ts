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
import {RestServiceFactory, SessionService, StorageService} from 'smarteditcontainer/services';
import {coreAnnotationsHelper, promiseHelper, IExtensiblePromise} from 'testhelpers';
import {annotationService, rarelyChangingContent, userEvictionTag, Cached, GatewayProxied, IRestService, User} from 'smarteditcommons';

describe('sessionService', () => {

	let sessionService: SessionService;
	let storageService: jasmine.SpyObj<StorageService>;
	let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;
	let cryptographicUtils: jasmine.SpyObj<any>;
	let _whoAmIResource: jasmine.SpyObj<IRestService<any>>;
	let _userService: jasmine.SpyObj<IRestService<any>>;

	const $q = promiseHelper.$q();
	const $log = jasmine.createSpyObj('$log', ['warn']);

	const WHO_AM_I_RESOURCE_URI: string = '/authorizationserver/oauth/whoami';
	const USER_DATA_URI = '/cmswebservices/v1/users/:userUid';

	const PREVIOUS_USERNAME_HASH: string = 'previousUsername';
	const MOCKED_USERNAME = "mocked_username";
	const SECOND_MOCKED_USERNAME = "second_mocked_username";
	const MOCK_USER_ID = 'MOCKED_UID';

	const MOCKED_WHO_AM_I_DATA = {
		displayName: 'MOCKED_DISPLAY_NAME',
		uid: MOCK_USER_ID
	};
	const MOCKED_USER_DATA = {
		uid: MOCK_USER_ID,
		readableLanguages: ['en', 'fr', 'de'],
		writeableLanguages: ['fr', 'de']
	};

	beforeEach(() => {
		storageService = jasmine.createSpyObj('storageService', ['setItem', 'getItem']);
		restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
		cryptographicUtils = jasmine.createSpyObj('cryptographicUtils', ['sha1Hash']);
		_whoAmIResource = jasmine.createSpyObj('_whoAmIResource', ['get']);
		_userService = jasmine.createSpyObj('_userService', ['get']);

		restServiceFactory.get.and.callFake((uri: string) => {
			if (uri === WHO_AM_I_RESOURCE_URI) {
				return _whoAmIResource;
			} else {
				return _userService;
			}
		});

		_whoAmIResource.get.and.returnValue($q.when(MOCKED_WHO_AM_I_DATA));
		_userService.get.and.returnValue($q.when(MOCKED_USER_DATA));

		coreAnnotationsHelper.init();
		sessionService = new SessionService($log, $q, restServiceFactory, WHO_AM_I_RESOURCE_URI, PREVIOUS_USERNAME_HASH, storageService, cryptographicUtils);
	});

	describe('initialization', function() {

		it('checks GatewayProxied', () => {
			const decoratorObj = annotationService.getClassAnnotation(SessionService, GatewayProxied);
			expect(decoratorObj).toEqual(['getCurrentUsername', 'getCurrentUserDisplayName', 'hasUserChanged', 'setCurrentUsername', 'getCurrentUser']);
		});

		it("creates a service factory based on the 'Who am I' Rest service and the users service", function() {
			expect(restServiceFactory.get).toHaveBeenCalledWith(WHO_AM_I_RESOURCE_URI);
			expect(restServiceFactory.get).toHaveBeenCalledWith(USER_DATA_URI);
		});
	});

	describe('getCurrentUserDisplayName()', function() {

		it('fetchs the user data through the "Who am I" service and returns user name when they are not cached yet', function() {

			// Given
			_whoAmIResource.get.and.returnValue($q.when(MOCKED_WHO_AM_I_DATA));

			// When
			const displayName: string = (sessionService.getCurrentUserDisplayName() as IExtensiblePromise<string>).value;

			// Assert
			expect(_whoAmIResource.get).toHaveBeenCalled();
			expect(restServiceFactory.get).toHaveBeenCalledWith(WHO_AM_I_RESOURCE_URI);
			expect(displayName).toBe(MOCKED_WHO_AM_I_DATA.displayName);
		});

		it('fetchs the user data through the "Who am I" service and returns user name when the cache has been reset', function() {

			// Given
			_whoAmIResource.get.and.returnValue($q.when(MOCKED_WHO_AM_I_DATA));

			// When
			sessionService.getCurrentUserDisplayName();

			_whoAmIResource.get.calls.reset();
			restServiceFactory.get.calls.reset();
			sessionService.resetCurrentUserData();

			const displayName = (sessionService.getCurrentUserDisplayName() as IExtensiblePromise<string>).value;

			// Assert
			expect(_whoAmIResource.get).toHaveBeenCalled();
			expect(displayName).toBe(MOCKED_WHO_AM_I_DATA.displayName);
		});
	});

	describe('getCurrentUsername()', function() {

		it('fetchs the user WhoAmI information through the "Who am I" service and returns user uid', function() {

			// Given
			_whoAmIResource.get.and.returnValue($q.when(MOCKED_WHO_AM_I_DATA));

			// When
			const userName = (sessionService.getCurrentUsername() as IExtensiblePromise<string>).value;

			// Assert
			expect(_whoAmIResource.get).toHaveBeenCalled();
			expect(restServiceFactory.get).toHaveBeenCalledWith(WHO_AM_I_RESOURCE_URI);
			expect(userName).toBe(MOCKED_WHO_AM_I_DATA.uid);

		});

		it('fetchs the user WhoAmI through the "Who am I" service and returns user uid when the cache has been reset', function() {

			// Given
			_whoAmIResource.get.and.returnValue($q.when(MOCKED_WHO_AM_I_DATA));

			// When
			sessionService.getCurrentUsername();

			_whoAmIResource.get.calls.reset();
			restServiceFactory.get.calls.reset();

			sessionService.resetCurrentUserData();

			const userName = (sessionService.getCurrentUsername() as IExtensiblePromise<string>).value;

			// Assert
			expect(_whoAmIResource.get).toHaveBeenCalled();
			expect(userName).toBe(MOCKED_WHO_AM_I_DATA.uid);
		});

	});

	describe('hasUserChanged() & setCurrentUsername', function() {

		beforeEach(() => {
			cryptographicUtils.sha1Hash.and.returnValue(MOCKED_USERNAME);
			_whoAmIResource.get.and.returnValue($q.when(MOCKED_WHO_AM_I_DATA));
		});

		it('returns false on first user connection', function() {
			// GIVEN
			(sessionService as any).cachedUserHash = null;
			storageService.getItem.and.returnValue($q.when(null));

			// THEN
			expect((sessionService.hasUserChanged() as IExtensiblePromise<boolean>).value).toBe(false);
		});

		it('returns false when the same user gets authenticated two times in a row', function() {
			// GIVEN
			(sessionService as any).cachedUserHash = MOCKED_USERNAME;
			storageService.getItem.and.returnValue($q.when(MOCKED_USERNAME));

			// WHEN
			const userChanged = (sessionService.hasUserChanged() as IExtensiblePromise<boolean>).value;

			// THEN
			expect(storageService.getItem).not.toHaveBeenCalled();
			expect(userChanged).toBe(false);
		});

		it('returns true when a new user gets authenticated', function() {
			// GIVEN
			(sessionService as any).cachedUserHash = SECOND_MOCKED_USERNAME;
			storageService.getItem.and.returnValue($q.when(MOCKED_USERNAME));

			// WHEN
			const userChanged = (sessionService.hasUserChanged() as IExtensiblePromise<boolean>).value;

			// THEN
			expect(storageService.getItem).not.toHaveBeenCalled();
			expect(userChanged).toBe(true);
		});

		it('GIVEN cache is refreshed and cleaned THEN user info is retrieved from cookie AND returns false when same user gets authenticated', () => {
			// GIVEN 
			(sessionService as any).cachedUserHash = null;
			storageService.getItem.and.returnValue($q.when(MOCKED_USERNAME));

			// WHEN
			const userChanged = (sessionService.hasUserChanged() as IExtensiblePromise<boolean>).value;

			// THEN
			expect(storageService.getItem).toHaveBeenCalledWith(PREVIOUS_USERNAME_HASH);
			expect(userChanged).toBe(false);
		});

		it('GIVEN cache is refreshed and cleaned THEN user info is retrieved from cookie AND return true when new user gets authenticated', () => {
			// GIVEN 
			(sessionService as any).cachedUserHash = null;
			storageService.getItem.and.returnValue($q.when(SECOND_MOCKED_USERNAME));

			// WHEN
			const userChanged = (sessionService.hasUserChanged() as IExtensiblePromise<boolean>).value;

			// THEN
			expect(storageService.getItem).toHaveBeenCalledWith(PREVIOUS_USERNAME_HASH);
			expect(userChanged).toBe(true);
		});

	});

	describe('getCurrentUser()', function() {

		it('fetches the user data through the "Who am I" and users service and returns user data', function() {
			// WHEN 
			const userData = (sessionService.getCurrentUser() as IExtensiblePromise<User>).value;

			// THEN 
			expect(_whoAmIResource.get).toHaveBeenCalled();
			expect(_userService.get).toHaveBeenCalledWith({
				userUid: MOCK_USER_ID
			});
			expect(userData).toEqual({
				uid: MOCK_USER_ID,
				displayName: MOCKED_WHO_AM_I_DATA.displayName,
				readableLanguages: MOCKED_USER_DATA.readableLanguages,
				writeableLanguages: MOCKED_USER_DATA.writeableLanguages
			});
		});
	});

	describe('_getCurrentUserData', function() {

		it('checks Cached annotation', () => {
			const decoratorObj = annotationService.getMethodAnnotation(SessionService, 'getCurrentUserData', Cached);
			expect(decoratorObj).toEqual(jasmine.objectContaining([{
				actions: [rarelyChangingContent],
				tags: [userEvictionTag]
			}]));
		});

	});

});
