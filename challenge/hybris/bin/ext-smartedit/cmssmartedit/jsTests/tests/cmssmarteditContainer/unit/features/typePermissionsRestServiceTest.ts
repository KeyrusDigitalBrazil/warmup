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

import {IRestService, IRestServiceFactory, ISessionService, TypedMap} from 'smarteditcommons';
import {TypePermissionsRestService} from 'cmscommons/services/TypePermissionsRestService';
import {promiseHelper, IExtensiblePromise} from 'testhelpers';

describe('TypePermissionsRestService', () => {

	// --------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------
	const restServiceFactory: jasmine.SpyObj<IRestServiceFactory> = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', ['get']);
	const typePermissionsRestResource: jasmine.SpyObj<IRestService<any>> = jasmine.createSpyObj<IRestService<any>>('typePermissionsRestResource', ['get']);

	const sessionService: any = jasmine.createSpyObj<ISessionService>('sessionService', ['getCurrentUsername']);

	const $q = promiseHelper.$q();
	const $log = jasmine.createSpyObj<angular.ILogService>('$log', ['error']);

	const typeCodeA = 'typeA';
	const typeCodeB = 'typeB';

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	let typePermissionsRestService: TypePermissionsRestService;
	let typeABPermissionResult: any;

	beforeEach(() => {

		typeABPermissionResult = {
			permissionsList: [
				{
					id: typeCodeA,
					permissions: [
						{
							key: 'read',
							value: "true"
						},
						{
							key: 'change',
							value: "false"
						},
						{
							key: 'create',
							value: "false"
						},
						{
							key: 'remove',
							value: "true"
						}
					]
				}, {
					id: typeCodeB,
					permissions: [
						{
							key: 'read',
							value: "true"
						},
						{
							key: 'change',
							value: "true"
						},
						{
							key: 'create',
							value: "false"
						},
						{
							key: 'remove',
							value: "false"
						}
					]
				}
			]
		};

		restServiceFactory.get.and.returnValue(typePermissionsRestResource);
		typePermissionsRestResource.get.and.returnValue($q.when(typeABPermissionResult));
		sessionService.getCurrentUsername.and.returnValue($q.when('someUser'));

		// call service
		typePermissionsRestService = new TypePermissionsRestService($log, $q, sessionService, restServiceFactory);

	});

	it(`GIVEN types exist
        WHEN hasCreatePermissionForTypes is called
        THEN should return TypedMap object`, () => {
			const promise = typePermissionsRestService.hasCreatePermissionForTypes([typeCodeA, typeCodeB]) as IExtensiblePromise<TypedMap<boolean>>;

			expect(promise.value).toEqual(jasmine.objectContaining({
				typeA: false,
				typeB: false
			}));

		});

	it(`GIVEN types exist
        WHEN hasReadPermissionForTypes is called
        THEN should return TypedMap object`, () => {

			const promise = typePermissionsRestService.hasReadPermissionForTypes([typeCodeA, typeCodeB]) as IExtensiblePromise<TypedMap<boolean>>;

			expect(promise.value).toEqual(jasmine.objectContaining({
				typeA: true,
				typeB: true
			}));

		});

	it(`GIVEN types exist
        WHEN hasUpdatePermissionForTypes is called
        THEN should return TypedMap object`, () => {

			const promise = typePermissionsRestService.hasUpdatePermissionForTypes([typeCodeA, typeCodeB]) as IExtensiblePromise<TypedMap<boolean>>;

			expect(promise.value).toEqual(jasmine.objectContaining({
				typeA: false,
				typeB: true
			}));

		});

	it(`GIVEN types exist
        WHEN hasDeletePermissionForTypes is called
        THEN should return TypedMap object`, () => {

			const promise = typePermissionsRestService.hasDeletePermissionForTypes([typeCodeA, typeCodeB]) as IExtensiblePromise<TypedMap<boolean>>;

			expect(promise.value).toEqual(jasmine.objectContaining({
				typeA: true,
				typeB: false
			}));

		});

	it(`GIVEN types doesnot exist
        WHEN hasDeletePermissionForTypes is called
        THEN promise should be rejected`, () => {

			typePermissionsRestResource.get.and.returnValue($q.reject("rejected"));

			const promise = typePermissionsRestService.hasDeletePermissionForTypes([typeCodeA, typeCodeB]) as IExtensiblePromise<string>;

			expect(promise.value).toEqual("rejected");

		});

	it(`GIVEN types exist
		WHEN hasAllPermissionsForTypes is called
		THEN should return TypedMap object`, () => {
			const promise = typePermissionsRestService.hasAllPermissionsForTypes([typeCodeA, typeCodeB]) as IExtensiblePromise<TypedMap<TypedMap<boolean>>>;

			console.log(promise.value);

			expect(promise.value).toEqual(jasmine.objectContaining({
				typeA: {
					read: true,
					change: false,
					create: false,
					remove: true
				},
				typeB: {
					read: true,
					change: true,
					create: false,
					remove: false
				}
			}));
		});
});