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
import {AuthorizationService} from "smarteditcommons/services/auth/AuthorizationService";
import {ISessionService, PermissionsRestService} from "smarteditcommons/services";
import {
	promiseHelper,
	IExtensiblePromise,
	LogHelper,
	PromiseType
} from "testhelpers";

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
describe('authorizationService', function() {

	// Service under test
	let authorizationService: AuthorizationService;

	// Mocks
	let mock$log: angular.ILogService;
	let mockSessionService: jasmine.SpyObj<ISessionService>;
	let mockPermissionsRestService: jasmine.SpyObj<PermissionsRestService>;

	// Test data
	const DUMMY_USERNAME = 'dummy_username';
	const READ_PERMISSION_NAME = 'smartedit.configurationcenter.read';
	const WRITE_PERMISSION_NAME = 'smartedit.configurationcenter.write';
	const DELETE_PERMISSION_NAME = 'smartedit.configurationcenter.delete';
	const UNKNOWN_PERMISSION_NAME = 'smartedit.configurationcenter.unknown';

	beforeEach(() => {
		mock$log = new LogHelper();
		mockSessionService = jasmine.createSpyObj<ISessionService>('mockSessionService', ['getCurrentUsername']);
		mockPermissionsRestService = jasmine.createSpyObj<PermissionsRestService>('mockPermissionsRestService', ['get']);
		authorizationService = new AuthorizationService(
			mock$log,
			mockSessionService,
			mockPermissionsRestService
		);
	});

	describe('hasGlobalPermissions', () => {

		it('throws error for invalid permissionNames', () => {
			expect(() => authorizationService.hasGlobalPermissions([]))
				.toThrowError(AuthorizationService.ERR_INVALID_PERMISSION_NAMES.message);
		});

		it('returns false when the query to the Global Permission REST API fails', () => {

			// Given
			const resolved = promiseHelper.buildPromise('resolvedUsername', PromiseType.RESOLVES, DUMMY_USERNAME);
			const rejected = promiseHelper.buildPromise('rejectedRestService', PromiseType.REJECTS, 'unable.to.get.permissions');
			mockSessionService.getCurrentUsername.and.returnValue(resolved);
			mockPermissionsRestService.get.and.returnValue(rejected);

			// When
			const result = authorizationService.hasGlobalPermissions([READ_PERMISSION_NAME]) as IExtensiblePromise<boolean>;

			// Then
			expect(result.value).toBe(false);
		});

		it('queries the Global Permissions REST API with the principal identifier and the permission names as a CSV string', () => {
			// Given
			const permissionNames = [READ_PERMISSION_NAME, WRITE_PERMISSION_NAME];
			mockSessionService.getCurrentUsername.and.returnValue(promiseHelper.$q().when(DUMMY_USERNAME));
			mockPermissionsRestService.get.and.returnValue(promiseHelper.$q().when({
				permissions: []
			}));

			// When
			authorizationService.hasGlobalPermissions(permissionNames);

			// Then
			expect(mockPermissionsRestService.get).toHaveBeenCalledWith(jasmine.objectContaining({
				user: DUMMY_USERNAME,
				permissionNames: permissionNames.join(',')
			}));
		});


		it('returns false when one permission is checked and is denied', () => {
			// Given
			const permissionNames = [DELETE_PERMISSION_NAME];
			const response = {
				id: 'global',
				permissions: [{
					key: DELETE_PERMISSION_NAME,
					value: 'false'
				}]
			};
			mockSessionService.getCurrentUsername.and.returnValue(promiseHelper.$q().when(DUMMY_USERNAME));
			mockPermissionsRestService.get.and.returnValue(promiseHelper.$q().when(response));

			// When
			const result = authorizationService.hasGlobalPermissions(permissionNames) as IExtensiblePromise<boolean>;

			// Then
			expect(result.value).toBe(false);
		});

		it('returns true when one permission is checked and is granted', () => {
			// Given
			const permissionNames = [READ_PERMISSION_NAME];
			const response = {
				id: 'global',
				permissions: [{
					key: READ_PERMISSION_NAME,
					value: 'true'
				}]
			};

			mockSessionService.getCurrentUsername.and.returnValue(promiseHelper.$q().when(DUMMY_USERNAME));
			mockPermissionsRestService.get.and.returnValue(promiseHelper.$q().when(response));

			// When
			const result = authorizationService.hasGlobalPermissions(permissionNames) as IExtensiblePromise<boolean>;

			// Then
			expect(result.value).toBe(true);
		});

		it('returns false when one of the multiple permissions checked is denied', () => {
			// Given
			const permissionNames = [READ_PERMISSION_NAME, DELETE_PERMISSION_NAME, WRITE_PERMISSION_NAME];
			const response = {
				id: 'global',
				permissions: [{
					key: READ_PERMISSION_NAME,
					value: 'true'
				}, {
					key: DELETE_PERMISSION_NAME,
					value: 'false'
				}, {
					key: WRITE_PERMISSION_NAME,
					valeu: 'true'
				}]
			};

			mockSessionService.getCurrentUsername.and.returnValue(promiseHelper.$q().when(DUMMY_USERNAME));
			mockPermissionsRestService.get.and.returnValue(promiseHelper.$q().when(response));

			// When
			const result = authorizationService.hasGlobalPermissions(permissionNames) as IExtensiblePromise<boolean>;

			// Then
			expect(result.value).toBe(false);
		});

		it('should return true if all of the multiple permissions checked are granted', () => {
			// Given
			const permissionNames = [READ_PERMISSION_NAME, WRITE_PERMISSION_NAME];
			const response = {
				id: 'global',
				permissions: [{
					key: READ_PERMISSION_NAME,
					value: 'true'
				}, {
					key: WRITE_PERMISSION_NAME,
					value: 'true'
				}]
			};

			mockSessionService.getCurrentUsername.and.returnValue(promiseHelper.$q().when(DUMMY_USERNAME));
			mockPermissionsRestService.get.and.returnValue(promiseHelper.$q().when(response));

			// When
			const result = authorizationService.hasGlobalPermissions(permissionNames) as IExtensiblePromise<boolean>;

			// Then
			expect(result.value).toBe(true);
		});

		it('should return false if a new requested permission is passed that does not exist in the permissions object returned from the API', () => {
			// Given
			const permissionNames = [READ_PERMISSION_NAME, UNKNOWN_PERMISSION_NAME];
			const response = {
				id: 'global',
				permissions: [{
					key: READ_PERMISSION_NAME,
					value: 'true'
				}]
			};

			mockSessionService.getCurrentUsername.and.returnValue(promiseHelper.$q().when(DUMMY_USERNAME));
			mockPermissionsRestService.get.and.returnValue(promiseHelper.$q().when(response));

			// When
			const result = authorizationService.hasGlobalPermissions(permissionNames) as IExtensiblePromise<boolean>;

			// Then
			expect(result.value).toBe(false);
		});
	});
});
